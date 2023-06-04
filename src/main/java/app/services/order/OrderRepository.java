package app.services.order;

import app.mongodb.MongoUtils;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.services.order.models.Order;
import app.shared.BaseAddress;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.conversions.Bson;

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

  public Uni<Order> getById(String userId, String id) {
    return getCollection().find(Filters.and(Filters.eq(Order.FIELD_ID, id), Filters.eq(Order.FIELD_USER_ID, userId))).toUni();
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


  public Uni<Void> updateStatus(ClientSession session, String id, String status) {
    return getCollection().updateOne(session, Filters.eq(Order.FIELD_ID, id), Updates.combine(
            Updates.set(Order.FIELD_MODIFIED_AT, LocalDateTime.now()),
            Updates.set(Order.FIELD_STATUS, status)))
        .replaceWithVoid();
  }

  public Uni<Order> updateShippingAddress(String trackingNumber, BaseAddress shippingAddress) {
    Bson filter = Filters.eq(Order.FIELD_TRACKINGNUMBER, trackingNumber);
    Bson update = Updates.set(Order.FIELD_SHIPPING_ADDRESS, shippingAddress);
    return MongoUtils.updateEntity(getCollection(), filter, update);
  }

  public Uni<Order> updateStatus(String id, String status) {
    Bson filter = Filters.eq(Order.FIELD_ID, id);
    Bson update = Updates.set(Order.FIELD_STATUS, status);
    return MongoUtils.updateEntity(getCollection(), filter, update);
  }

  public Uni<Order> archive(ClientSession session, Order order) {
    return mongoClient.getCollection(MongoCollections.ORDERS_ARCHIVE_COLLECTION, Order.class).insertOne(session, order).map(insertOneResult -> order);
  }

  public Uni<DeleteResult> delete(ClientSession session, String id) {
    return getCollection().deleteOne(session, Filters.eq(Order.FIELD_ID, id));
  }
  public Uni<DeleteResult> delete(String id) {
    return getCollection().deleteOne(Filters.eq(Order.FIELD_ID, id));
  }


}