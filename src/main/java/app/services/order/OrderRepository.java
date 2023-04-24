package app.services.order;

import app.mongodb.MongoUtils;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.services.order.models.Order;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class OrderRepository {
  @Inject
  MongoCollectionWrapper mongoClient;

  public ReactiveMongoCollection<Order> getCollection() {
    return mongoClient.getCollection(MongoCollections.ORDERS_COLLECTION, Order.class);
  }

  public Uni<List<Order>> getList() {
    return getCollection().find().collect().asList();
  }

  public Uni<Order> getById(String id) {
    return getCollection().find(Filters.eq(Order.FIELD_ID, id)).toUni();
  }

  public Uni<Order> getByTrackingNumber(String trackingNumber) {
    return getCollection().find(Filters.eq(Order.FIELD_TRACKINGNUMBER, trackingNumber)).toUni();
  }

  public Uni<List<Order>> getListByManufacturerId(String id) {
    return getCollection().find(Filters.eq(Order.FIELD_USER_ID, id)).collect().asList();
  }

  public Uni<Order> add(ClientSession session, Order order) {
    return MongoUtils.addEntity(session, getCollection(), order);
  }

  public Uni<Void> updateStatusFromTracking(String trackingNumber, String status) {
    return getCollection().updateOne(Filters.eq(Order.FIELD_TRACKINGNUMBER, trackingNumber), Updates.combine(
            Updates.set(Order.FIELD_MODIFIED_AT, LocalDateTime.now()),
            Updates.set(Order.FIELD_STATUS, status)))
        .replaceWithVoid();
  }

  public Uni<Void> updateStatus(String id, String status) {
    return getCollection().updateOne(Filters.eq(Order.FIELD_ID, id), Updates.combine(
            Updates.set(Order.FIELD_MODIFIED_AT, LocalDateTime.now()),
            Updates.set(Order.FIELD_STATUS, status)))
        .replaceWithVoid();
  }
  public Uni<Void> updateStatus(ClientSession session, String id, String status) {
    return getCollection().updateOne(session, Filters.eq(Order.FIELD_ID, id), Updates.combine(
            Updates.set(Order.FIELD_MODIFIED_AT, LocalDateTime.now()),
            Updates.set(Order.FIELD_STATUS, status)))
        .replaceWithVoid();
  }

  public Uni<DeleteResult> delete(String id) {
    return getCollection().deleteOne(Filters.eq(Order.FIELD_ID, id));
  }

  public Uni<DeleteResult> delete(ClientSession session, String id) {
    return getCollection().deleteOne(session, Filters.eq(Order.FIELD_ID, id));
  }
}