package app.services.shoppingcart;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.helpers.ProductUpdateHelper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoSessionWrapper;
import app.mongodb.MongoUtils;
import app.services.order.OrderService;
import app.services.order.models.CreateOrder;
import app.services.order.models.OrderDetails;
import app.services.product.ProductService;
import app.services.product.exceptions.ProductException;
import app.services.product.models.ProductReference;
import app.services.shoppingcart.exceptions.ShoppingCartException;
import app.services.shoppingcart.models.CreateShoppingCart;
import app.services.shoppingcart.models.ShoppingCart;
import app.shared.SuccessResponse;
import app.utils.Utils;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.scheduler.Scheduled;
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
  OrderService orderService;

  @Inject
  ProductUpdateHelper productUpdateHelper;

  @Inject
  MongoSessionWrapper sessionWrapper;

  public Uni<ShoppingCart> getById(String id) {
    return ShoppingCart.findById(id)
        .onItem().ifNull().failWith(new ShoppingCartException(ShoppingCartException.SHOPPING_CART_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(ShoppingCart.class));
  }

  public Uni<PaginatedResponse<ShoppingCart>> getList(PaginationWrapper wrapper) {
    return MongoUtils.getPaginatedItems(repository.getCollection(), wrapper);
  }

  public Uni<ShoppingCart> getByUserId(String userId) {
    return ShoppingCart.find(ShoppingCart.FIELD_USER_ID, userId).firstResult()
        .onItem().ifNull().failWith(new ShoppingCartException(ShoppingCartException.SHOPPING_CART_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(ShoppingCart.class));
  }

  public Uni<ShoppingCart> add(ClientSession session, CreateShoppingCart createShoppingCart) {
    return validator.validate(createShoppingCart)
        .replaceWith(ShoppingCartMapper.INSTANCE.from(createShoppingCart))
        .call(shoppingCart -> repository.add(session, shoppingCart));
  }

//  @Scheduled(every = "15m")
  public Uni<Void> clearNotUpdatedCarts() {
    return repository.clearNotUpdatedCarts();
  }

  public ShoppingCart updateShoppingCart(ShoppingCart shoppingCart, ProductReference productReference) {
    Optional<ProductReference> optionalProductReference = shoppingCart.getProducts().stream()
        .filter(p -> p.id.equals(productReference.id)).findFirst();
    if (optionalProductReference.isPresent()) {
      optionalProductReference.get().setQuantity(productReference.getQuantity());
    } else {
      shoppingCart.getProducts().add(productReference);
    }

    double totalPrice = shoppingCart.getProducts().stream()
        .mapToDouble(item -> item.getQuantity() * item.getPrice())
        .sum();

    shoppingCart.setTotal(totalPrice);
    return shoppingCart;
  }

  public Uni<ShoppingCart> update(ClientSession session, String userId, ProductReference productReference) {
    return validateAndUpdate(userId, productReference)
        .flatMap(shoppingCart -> repository.update(session, userId, shoppingCart));
  }

  public Uni<ShoppingCart> update(String userId, ProductReference productReference) {
    return  validateAndUpdate(userId, productReference)
        .flatMap(MongoUtils::updateEntity);
  }

  private Uni<ShoppingCart> validateAndUpdate(String userId, ProductReference productReference) {
    return validator.validate(productReference)
        .replaceWith(productService.getById(productReference.id))
        .onFailure().transform(transformToBadRequest())
        .flatMap(product -> {
          if (productReference.getQuantity() > product.getStockQuantity()) {
            return Uni.createFrom().failure(new ProductException(ProductException.NOT_ENOUGH_STOCK, Response.Status.BAD_REQUEST));
          }
          return Uni.createFrom().item(product);
        })
        .replaceWith(this.getByUserId(userId))
        .onFailure().transform(transformToBadRequest())
        .map(shoppingCart -> this.updateShoppingCart(shoppingCart, productReference));
  }

  public Uni<Void> updateAllCarts(ClientSession session, ProductReference product) {
    return productUpdateHelper.updateProductOccurrences(session, MongoCollections.SHOPPING_CARTS_COLLECTION, ShoppingCart.class, product);
  }

  public Uni<ShoppingCart> removeProductFromCart(String userId, ProductReference productReference) {
    return repository.removeProductFromCart(userId, productReference);
  }

  public Uni<SuccessResponse> emptyShoppingCart(String userId) {
    return repository.emptyShoppingCart(userId)
        .onFailure().transform(transformToBadRequest())
        .replaceWith(SuccessResponse.toSuccessResponse());
  }

  public Uni<ShoppingCart> buyShoppingCart(String userId, OrderDetails orderDetails) {
    return this.getByUserId(userId).onFailure().transform(transformToBadRequest())
        .flatMap(shoppingCart -> {
          CreateOrder createOrder = ShoppingCartMapper.from(shoppingCart, orderDetails);
          return Uni.createFrom().item(createOrder)
              .flatMap(orderToCreate -> sessionWrapper.getSession()
                  .flatMap(
                      session -> orderService.add(session, orderToCreate)
                          .replaceWith(() -> productService.decreaseQuantity(session, orderToCreate.getProducts()))
                          .replaceWith(this.emptyShoppingCart(userId))
                          .replaceWith(Uni.createFrom().publisher(session.commitTransaction()))
                          .replaceWith(shoppingCart)
                          .eventually(session::close)
                  ));
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