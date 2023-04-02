package app.services.accounts;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.accounts.models.User;
import app.services.auth.models.State;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
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

  public Uni<User> add(User user){
    return MongoUtils.addEntity(getCollection(), user);
  }

  public Uni<User> update(String id, User user) {
    return MongoUtils.updateEntity(getCollection(), Filters.eq(User.FIELD_ID, id), user);
  }

  public Uni<User> updateState(String id, State state) {
    return getCollection().findOneAndUpdate(Filters.eq(User.FIELD_ID, id),
        Updates.set(User.FIELD_STATE, state));
  }

  public Uni<DeleteResult> delete(String id) {
    return getCollection().deleteOne(Filters.eq(User.FIELD_ID, id));
  }

}
