package app.services.accounts;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserRepository {

  @Inject
  MongoCollectionWrapper mongoClient;

  public ReactiveMongoCollection<User> getCollection() {
    return mongoClient.getCollection(MongoCollections.USERS_COLLECTION, User.class);
  }

  public Uni<User> getById(String id) {
    return getCollection().find(Filters.eq(User.FIELD_ID, id)).toUni();
  }

  public Uni<User> getWithEmail(String email) {
    return getCollection().find(Filters.eq(User.FIELD_EMAIL, email)).toUni();
  }
}
