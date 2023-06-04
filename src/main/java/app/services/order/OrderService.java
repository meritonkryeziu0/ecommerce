package app.services.order;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.helpers.ProductUpdateHelper;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoSessionWrapper;
import app.mongodb.MongoUtils;
import app.proto.MutinyNotificationGrpc;
import app.proto.MutinyTrackerGrpc;
import app.services.order.exceptions.OrderException;
import app.services.order.models.*;
import app.services.product.ProductService;
import app.services.product.models.ProductReference;
import app.services.product.models.Rating;
import app.shared.BaseAddress;
import app.shared.SuccessResponse;
import app.utils.Utils;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class OrderService {
  @Inject
  CustomValidator validator;
  @Inject
  OrderRepository repository;
  @Inject
  ProductUpdateHelper productUpdateHelper;
  @GrpcClient(value = "tracker")
  @Inject
  MutinyTrackerGrpc.MutinyTrackerStub trackerStub;
  @GrpcClient(value = "notify")
  @Inject
  MutinyNotificationGrpc.MutinyNotificationStub notificationStub;
  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;
  @Inject
  ProductService productService;
  @Inject
  MongoSessionWrapper sessionWrapper;

  public ReactiveMongoCollection<Order> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.ORDERS_COLLECTION, Order.class);
  }

  public Uni<List<Order>> getList() {
    return Order.listAll();
  }

  public Uni<PaginatedResponse<Order>> getList(PaginationWrapper wrapper) {
    return MongoUtils.getPaginatedItems(getCollection(), wrapper);
  }

  public Uni<List<Order>> getList(String userId) {
    return Order.find(Order.FIELD_USER_ID, userId).list();
  }

  public Uni<Order> getById(String id) {
    return Order.findById(id)
        .onItem().ifNull().failWith(new OrderException(OrderException.ORDER_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(Order.class));
  }

  public Uni<Order> getById(String userId, String id) {
    return repository.getById(userId, id)
        .onItem().ifNull().failWith(new OrderException(OrderException.ORDER_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(Order.class));
  }

  public Uni<Order> getByTrackingNumber(String trackingNumber) {
    return repository.getByTrackingNumber(trackingNumber)
        .onItem().ifNull().failWith(new OrderException(OrderException.ORDER_NOT_FOUND, Response.Status.NOT_FOUND));
  }

  public Uni<Order> add(CreateOrder createOrder) {
    return validator.validate(createOrder)
        .replaceWith(OrderMapper.INSTANCE.from(createOrder))
        .call(MongoUtils::addEntity);
  }

  public Uni<Order> add(ClientSession session, CreateOrder createOrder) {
    return validator.validate(createOrder)
        .replaceWith(OrderMapper.INSTANCE.from(createOrder))
        .flatMap(order -> trackerStub.addTracking(OrderMapper.toTracking(order))
            .map(trackingReply -> {
              order.setTracking(new Tracking(trackingReply));
              return order;
            }))
        .flatMap(order -> repository.add(session, order));
  }

  public Uni<SuccessResponse> delete(String id) {
    return Order.deleteById(id).replaceWith(SuccessResponse.toSuccessResponse());
  }

  public Uni<Order> update(String id, UpdateOrder updateOrder) {
    return validator.validate(updateOrder)
        .replaceWith(this.getById(id))
        .onFailure().transform(transformToBadRequest())
        .map(OrderMapper.from(updateOrder))
        .call(MongoUtils::updateEntity);
  }

  public Uni<Void> updateAllOrders(ClientSession session, ProductReference productReference) {
    return productUpdateHelper.updateProductOccurrences(session, MongoCollections.ORDERS_COLLECTION, Order.class, productReference);
  }

  public Uni<Order> editShippingAddress(String id, BaseAddress shippingAddress) {
    return validator.validate(shippingAddress)
        .flatMap(address -> this.getById(id))
        .onFailure().transform(transformToBadRequest())
        .flatMap(order -> {
          if (order.getStatus().equals(Order.OrderStatuses.DISPATCHED.name())
              || order.getStatus().equals(Order.OrderStatuses.DELIVERED.name())) {
            return Uni.createFrom().failure(new OrderException(OrderException.ORDER_ADDRESS_NOT_CHANGED, Response.Status.BAD_REQUEST));
          }
          return Uni.createFrom().item(order);
        })
        .flatMap(order -> repository.updateShippingAddress(order.getTracking().getTrackingNumber(), shippingAddress))
        .flatMap(order -> trackerStub.editShippingAddress(
                OrderGrpcMapper.toEditShippingAddressRequest(
                    order.getTracking().getTrackingNumber(),
                    shippingAddress))
            .map(ignore -> order));
  }

  public Uni<SuccessResponse> updateStatus(String id, UpdateOrderStatus status) {
    if (Order.OrderStatuses.contains(status.getStatus())) {
      return validator.validate(status)
          .replaceWith(this.getById(id))
          .flatMap(order -> notificationStub.updateStatusNotification(OrderGrpcMapper.toNotifyUserRequest(order)))
          .replaceWith(repository.updateStatus(id, status.getStatus().toUpperCase())
              .map(ignore -> SuccessResponse.toSuccessResponse()));
    }
    return Uni.createFrom().failure(new OrderException(OrderException.ORDER_STATUS_INVALID, Response.Status.BAD_REQUEST));
  }

  public Uni<SuccessResponse> cancelOrder(String id) {
    return this.getById(id)
        .flatMap(order -> {
          if (!order.getStatus().equals(Order.OrderStatuses.DISPATCHED.name()) && !(order.getStatus().equals(Order.OrderStatuses.DELIVERED.name()))) {
            order.setStatus(Order.OrderStatuses.CANCELLED.name());
            return sessionWrapper.getSession()
                .flatMap(clientSession -> notificationStub.updateStatusNotification(OrderGrpcMapper.toNotifyUserRequest(order))
                    .flatMap(ignore -> repository.archive(clientSession, order))
                    .flatMap(ignore -> productService.increaseQuantity(clientSession, order.getProducts()))
                    .flatMap(ignore -> repository.delete(clientSession, id))
                    .replaceWith(Uni.createFrom().publisher(clientSession.commitTransaction())
                        .eventually(clientSession::close)
                        .replaceWith(SuccessResponse.toSuccessResponse())));
          }
          return Uni.createFrom().failure(new OrderException(OrderException.ORDER_NOT_CANCELLED, Response.Status.BAD_REQUEST));
        });
  }

  public Uni<Void> rateProduct(String id, String productId, Rating rating) {
    return validator.validate(rating)
        .flatMap(ratingItem -> this.getById(id))
        .flatMap(order -> {
          if (order.getStatus().equals(Order.OrderStatuses.DELIVERED.name())) {
            return Uni.createFrom().item(order);
          }
          return Uni.createFrom().failure(new OrderException(OrderException.ORDER_NOT_DELIVERED, Response.Status.BAD_REQUEST));
        })
        .flatMap(order -> productService.rateProduct(productId, rating))
        .replaceWithVoid();
  }

  public Uni<Void> updateStatusFromTracking(String trackingNumber, String status) {
    if (Order.OrderStatuses.contains(status)) {
      return this.getByTrackingNumber(trackingNumber)
          .call(order -> repository.updateStatusFromTracking(trackingNumber, status))
          .flatMap(order -> notificationStub.updateStatusNotification(OrderGrpcMapper.toNotifyUserRequest(order)))
          .replaceWith(repository.updateStatus(trackingNumber, status))
          .replaceWithVoid();
    }
    return repository.updateStatusFromTracking(trackingNumber, status)
        .replaceWith(this.getByTrackingNumber(trackingNumber))
        .flatMap(order -> notificationStub.updateStatusNotification(OrderGrpcMapper.toNotifyUserRequest(order)))
        .replaceWithVoid();
  }


  private Function<Throwable, Throwable> transformToBadRequest() {
    return throwable -> {
      if (throwable instanceof BaseException) {
        return new OrderException(OrderException.ORDER_NOT_FOUND, Response.Status.BAD_REQUEST);
      }
      return throwable;
    };
  }

}

