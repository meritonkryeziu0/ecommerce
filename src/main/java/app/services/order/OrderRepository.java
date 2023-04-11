package app.services.order;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.order.models.Order;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
    return MongoUtils.getEntityById(getCollection(), id);
  }

  public Uni<Order> add(Order order) {
    return MongoUtils.addEntity(getCollection(), order);
  }

  public Uni<Order> update(String id, Order order) {
    return MongoUtils.updateEntity(getCollection(), Filters.eq(Order.FIELD_ID, id), order);
  }

  public Uni<DeleteResult> delete(String id) {
    return getCollection().deleteOne(Filters.eq(Order.FIELD_ID, id));
  }
}