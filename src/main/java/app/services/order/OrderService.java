package app.services.order;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoSessionWrapper;
import app.mongodb.MongoUtils;
import app.proto.MutinyNotificationGrpc;
import app.proto.MutinyTrackerGrpc;
import app.services.order.exceptions.OrderException;
import app.services.order.models.CreateOrder;
import app.services.order.models.Order;
import app.services.order.models.UpdateOrder;
import app.services.order.models.UpdateOrderStatus;
import app.services.product.ProductService;
import app.shared.BaseAddress;
import app.shared.SuccessResponse;
import app.utils.Utils;
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

  public Uni<Order> getById(String id) {
    return Order.findById(id)
        .onItem().ifNull().failWith(new OrderException(OrderException.ORDER_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(Order.class));
  }

  public Uni<Order> add(CreateOrder createOrder) {
    return validator.validate(createOrder)
        .replaceWith(OrderMapper.INSTANCE.from(createOrder))
        .call(MongoUtils::addEntity);
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

  private Function<Throwable, Throwable> transformToBadRequest() {
    return throwable -> {
      if (throwable instanceof BaseException) {
        return new OrderException(OrderException.ORDER_NOT_FOUND, Response.Status.BAD_REQUEST);
      }
      return throwable;
    };
  }
}