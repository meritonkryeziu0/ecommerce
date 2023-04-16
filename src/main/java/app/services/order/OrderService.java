package app.services.order;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.order.exceptions.OrderException;
import app.services.order.models.CreateOrder;
import app.services.order.models.Order;
import app.services.order.models.UpdateOrder;
import app.shared.SuccessResponse;
import app.utils.Utils;
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
  MongoCollectionWrapper mongoCollectionWrapper;

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
        .replaceWith(OrderMapper.from(createOrder))
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

  private Function<Throwable, Throwable> transformToBadRequest() {
    return throwable -> {
      if (throwable instanceof BaseException) {
        return new OrderException(OrderException.ORDER_NOT_FOUND, Response.Status.BAD_REQUEST);
      }
      return throwable;
    };
  }
}