package app.services.shoppingcart;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.services.product.ProductService;
import app.services.product.exceptions.ProductException;
import app.services.product.models.ProductReference;
import app.services.shoppingcart.exceptions.ShoppingCartException;
import app.services.shoppingcart.models.CreateShoppingCart;
import app.services.shoppingcart.models.ShoppingCart;
import app.utils.Utils;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.common.constraint.Nullable;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Function;

@ApplicationScoped
public class ShoppingCartService {
  @Inject
  CustomValidator validator;

  @Inject
  ShoppingCartRepository repository;

  @Inject
  ProductService productService;

  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;

  public ReactiveMongoCollection<ShoppingCart> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.SHOPPING_CARTS_COLLECTION, ShoppingCart.class);
  }

  public Uni<PaginatedResponse<ShoppingCart>> getList(PaginationWrapper wrapper) {
    return MongoUtils.getPaginatedItems(getCollection(), wrapper);
  }

  public Uni<ShoppingCart> getByUserId(String userId) {
    return ShoppingCart.find(ShoppingCart.FIELD_USER_ID, userId).firstResult()
        .onItem().ifNull().failWith(new ShoppingCartException(ShoppingCartException.SHOPPING_CART_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(ShoppingCart.class));
  }

  public Uni<ShoppingCart> add(ClientSession session, CreateShoppingCart createShoppingCart) {
    return validator.validate(createShoppingCart)
        .replaceWith(ShoppingCartMapper.from(createShoppingCart))
        .call(shoppingCart -> repository.add(session, shoppingCart));
  }

  private ShoppingCart updateShoppingCart(ShoppingCart shoppingCart, ProductReference productReference) {
    Optional<ProductReference> optionalProductReference = shoppingCart.getProducts().stream()
        .filter(p -> p._id.equals(productReference._id)).findFirst();
    if (optionalProductReference.isPresent()) {
      optionalProductReference.get().setQuantity(optionalProductReference.get().getQuantity() + productReference.getQuantity());
    } else {
      shoppingCart.getProducts().add(productReference);
    }
    shoppingCart.setTotal(shoppingCart.getTotal() + productReference.getQuantity() * productReference.getPrice());
    return shoppingCart;
  }

  public Uni<ShoppingCart> update(@Nullable ClientSession session, String userId, ProductReference productReference) {
    return validator.validate(productReference)
        .replaceWith(productService.getById(productReference._id))
        .onFailure().transform(transformToBadRequest())
        .flatMap(product -> {
          if (productReference.getQuantity() > product.getStockQuantity()) {
            return Uni.createFrom().failure(new ProductException(ProductException.NOT_ENOUGH_STOCK, Response.Status.BAD_REQUEST));
          }
          return Uni.createFrom().item(product);
        })
        .replaceWith(this.getByUserId(userId))
        .onFailure().transform(transformToBadRequest())
        .map(shoppingCart -> this.updateShoppingCart(shoppingCart, productReference))
        .flatMap(shoppingCart -> {
          if(Utils.isNull(session)){
            return MongoUtils.updateEntity(shoppingCart);
          }
          return repository.update(session, userId, shoppingCart);
        });
  }

  private Function<Throwable, Throwable> transformToBadRequest() {
    return throwable -> {
      if (throwable instanceof BaseException) {
        return new ShoppingCartException(ShoppingCartException.PRODUCT_NOT_ADDED, Response.Status.BAD_REQUEST);
      }
      return throwable;
    };
  }


}