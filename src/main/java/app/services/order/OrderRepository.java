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


}