package app.services.order;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
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

@ApplicationScoped
public class OrderRepository {
  @Inject
  MongoCollectionWrapper mongoClient;

  public ReactiveMongoCollection<Order> getCollection() {
    return mongoClient.getCollection(MongoCollections.ORDERS_COLLECTION, Order.class);
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
}