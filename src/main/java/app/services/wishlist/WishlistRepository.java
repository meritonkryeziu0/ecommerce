package app.services.wishlist;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.product.models.Product;
import app.services.product.models.ProductReference;
import app.services.wishlist.models.Wishlist;
import com.mongodb.client.model.*;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class WishlistRepository {
  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;

  public ReactiveMongoCollection<Wishlist> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.WISHLIST_COLLECTION, Wishlist.class);
  }

  public Uni<Wishlist> getById(String id) {
    return MongoUtils.getEntityById(getCollection(), id);
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

  public Uni<Wishlist> updateProductQuantity(String userId, String productId, int quantity) {
    if (quantity > 0) {
      return incrementProductQuantity(userId, productId, quantity);
    }
    return decrementProductQuantity(userId, productId, quantity);
  }

  private Uni<Wishlist> incrementProductQuantity(String userId, String productId, int quantity) {
    return getCollection().findOneAndUpdate(Filters.eq(Wishlist.FIELD_USER_ID, userId),
        Updates.combine(Updates.inc("products.$[elem].quantity", quantity)),
        new FindOneAndUpdateOptions().arrayFilters(List.of(Filters.eq("elem._id", productId)))
            .returnDocument(ReturnDocument.AFTER));
  }

  private Uni<Wishlist> decrementProductQuantity(String userId, String productId, int quantity) {
    return getCollection().findOneAndUpdate(Filters.eq(Wishlist.FIELD_USER_ID, userId),
        Updates.combine(Updates.inc("products.$[elem].quantity", quantity)),
        new FindOneAndUpdateOptions().arrayFilters(List.of(Filters.and(Filters.eq("elem._id", productId),
            Filters.gt("elem.quantity", Math.abs(quantity)),
            Filters.gt("elem.quantity", 1)))).returnDocument(ReturnDocument.AFTER));
  }

  public Uni<Wishlist> removeProductFromWishlist(String userId, ProductReference productReference) {
    return getCollection().findOneAndUpdate(Filters.eq(Wishlist.FIELD_USER_ID, userId),
        Updates.pull(Wishlist.FIELD_PRODUCTS, new Document(Product.FIELD_ID, productReference._id)), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
  }

  public Uni<Wishlist> removeProductFromWishlist(ClientSession session, String userId, ProductReference productReference) {
    return getCollection().findOneAndUpdate(session, Filters.eq(Wishlist.FIELD_USER_ID, userId),
        Updates.pull(Wishlist.FIELD_PRODUCTS, new Document(Product.FIELD_ID, productReference._id)), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
  }

  public Uni<Void> emptyWishlist(String userId) {
    return getCollection().findOneAndUpdate(Filters.eq(Wishlist.FIELD_USER_ID, userId),
        Updates.set(Wishlist.FIELD_PRODUCTS, new ArrayList<>())).replaceWithVoid();
  }
}