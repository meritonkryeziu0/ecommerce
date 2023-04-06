package app.services.wishlist;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.wishlist.models.Wishlist;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;

@ApplicationScoped
public class WishlistRepository {
  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;

  public ReactiveMongoCollection<Wishlist> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.SHOPPING_CARTS_COLLECTION, Wishlist.class);
  }

  public Uni<Wishlist> getById(String id) {
    return getCollection().find(Filters.eq(Wishlist.FIELD_ID, id)).toUni();
  }

  public Uni<Wishlist> getByUserId(String userId) {
    return getCollection().find(Filters.eq(Wishlist.FIELD_USER_ID, userId)).toUni();
  }

  public Uni<Wishlist> add(ClientSession session, Wishlist wishlist) {
    return MongoUtils.addEntity(session, getCollection(), wishlist);
  }

  public Uni<Wishlist> update(String userId, Wishlist wishlist) {
    return MongoUtils.updateEntity(getCollection(), Filters.eq(Wishlist.FIELD_USER_ID, userId), wishlist);
  }

  public Uni<Void> emptyWishlist(String userId) {
    return getCollection().findOneAndUpdate(Filters.eq(Wishlist.FIELD_USER_ID, userId),
        Updates.set(Wishlist.FIELD_PRODUCTS, new ArrayList<>())).replaceWithVoid();
  }
}
