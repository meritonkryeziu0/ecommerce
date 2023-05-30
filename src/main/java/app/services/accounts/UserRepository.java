package app.services.accounts;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.accounts.models.ShippingAddress;
import app.services.accounts.models.User;
import app.services.auth.models.State;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class UserRepository {

  @Inject
  MongoCollectionWrapper mongoClient;

  public ReactiveMongoCollection<User> getCollection() {
    return mongoClient.getCollection(MongoCollections.USERS_COLLECTION, User.class);
  }

  public Uni<User> add(ClientSession session, User user) {
    return MongoUtils.addEntity(session, getCollection(), user);
  }

  public Uni<User> updateState(String id, State state) {
    return getCollection().findOneAndUpdate(Filters.eq(User.FIELD_ID, id),
        Updates.set(User.FIELD_STATE, state));
  }

  public Uni<DeleteResult> delete(String id) {
    return getCollection().deleteOne(Filters.eq(User.FIELD_ID, id));
  }

  public Uni<User> addShippingAddress(String id, ShippingAddress shippingAddress) {
    return getCollection().findOneAndUpdate(Filters.eq(User.FIELD_ID, id),
        Updates.push(User.FIELD_SHIPPING_ADDRESSES, shippingAddress),
        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
  }


  public Uni<User> editShippingAddress(String id, String shippingId, ShippingAddress shippingAddress) {
    return getCollection().findOneAndUpdate(Filters.eq(User.FIELD_ID, id),
        Updates.combine(Updates.set("shippingAddresses.$[elem]", shippingAddress)),
        new FindOneAndUpdateOptions().arrayFilters(List.of(Filters.eq("elem._id", shippingId)))
            .returnDocument(ReturnDocument.AFTER));
  }

  public Uni<User> deleteShippingAddress(String id, ShippingAddress shippingAddress) {
    return getCollection().findOneAndUpdate(Filters.eq(User.FIELD_ID, id),
        Updates.pull(User.FIELD_SHIPPING_ADDRESSES, shippingAddress),
        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
  }

  public Uni<User> setUserShippingAddresses(String userId, List<ShippingAddress> addresses) {
    return getCollection().findOneAndUpdate(Filters.eq(User.FIELD_ID, userId),
        Updates.set(User.FIELD_SHIPPING_ADDRESSES, addresses),
        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
  }
}