package app.services.shoppingcart;

import app.mongodb.MongoUtils;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.services.product.models.ProductReference;
import app.services.shoppingcart.models.ShoppingCart;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;

@ApplicationScoped
public class ShoppingCartRepository {
  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;

  public ReactiveMongoCollection<ShoppingCart> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.SHOPPING_CARTS_COLLECTION, ShoppingCart.class);
  }

  public Uni<ShoppingCart> add(ClientSession session, ShoppingCart shoppingCart) {
    return MongoUtils.addEntity(session, getCollection(), shoppingCart);
  }

  public Uni<ShoppingCart> update(ClientSession session, String userId, ShoppingCart shoppingCart) {
    return MongoUtils.updateEntity(session, getCollection(), Filters.eq(ShoppingCart.FIELD_USER_ID, userId), shoppingCart);
  }

  public Uni<ShoppingCart> removeProductFromCart(String userId, ProductReference productReference) {
    return getCollection().findOneAndUpdate(
        Filters.eq(ShoppingCart.FIELD_USER_ID, userId),
        Updates.combine(
            Updates.inc(ShoppingCart.FIELD_TOTAL, -(productReference.getPrice()) * productReference.getQuantity()),
            Updates.pull(ShoppingCart.FIELD_PRODUCTS, productReference)));
  }

  public Uni<Void> clearNotUpdatedCarts() {
    return getCollection().updateMany(Filters.lt(ShoppingCart.FIELD_MODIFIED_AT, LocalDateTime.now().minusMinutes(5)),
        Updates.combine(Updates.set(ShoppingCart.FIELD_PRODUCTS, new ArrayList<>()),
            Updates.set(ShoppingCart.FIELD_TOTAL, 0))).replaceWithVoid();
  }

  public Uni<Void> emptyShoppingCart(String id) {
    return getCollection().findOneAndUpdate(Filters.eq(ShoppingCart.FIELD_USER_ID, id),
        Updates.combine(Updates.set(ShoppingCart.FIELD_PRODUCTS, new ArrayList<>()),
            Updates.set(ShoppingCart.FIELD_TOTAL, 0))).replaceWithVoid();
  }
}