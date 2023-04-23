package app.services.wishlist;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.mongodb.MongoSessionWrapper;
import app.mongodb.MongoUtils;
import app.services.product.ProductService;
import app.services.product.exceptions.ProductException;
import app.services.product.models.ProductReference;
import app.services.shoppingcart.ShoppingCartService;
import app.services.wishlist.exceptions.WishlistException;
import app.services.wishlist.models.CreateWishlist;
import app.services.wishlist.models.Wishlist;
import app.shared.SuccessResponse;
import app.utils.Utils;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Function;

@ApplicationScoped
public class WishlistService {
  @Inject
  ProductService productService;
  @Inject
  ShoppingCartService shoppingCartService;
  @Inject
  WishlistRepository repository;
  @Inject
  MongoSessionWrapper sessionWrapper;
  @Inject
  CustomValidator validator;

  public Uni<Wishlist> getByUserId(String userId) {
    return Wishlist.find(Wishlist.FIELD_USER_ID, userId).firstResult()
        .onItem().ifNull()
        .failWith(new WishlistException(WishlistException.WISHLIST_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(Wishlist.class));
  }

  public Uni<Wishlist> add(ClientSession session, CreateWishlist createWishlist) {
    return validator.validate(createWishlist)
        .replaceWith(WishlistMapper.from(createWishlist))
        .flatMap(wishlist -> repository.add(session, wishlist));
  }

  public Uni<Wishlist> update(String userId, ProductReference productReference) {
    return validator.validate(productReference)
        .replaceWith(productService.getById(productReference._id))
        .onFailure().transform(transformToBadRequest(ProductException.PRODUCT_NOT_FOUND, Response.Status.NOT_FOUND))
        .flatMap(product -> {
          if (productReference.getQuantity() > product.getStockQuantity()) {
            return Uni.createFrom().failure(new ProductException(ProductException.NOT_ENOUGH_STOCK, Response.Status.BAD_REQUEST));
          }
          return Uni.createFrom().item(product);
        })
        .onFailure().transform(transformToBadRequest(WishlistException.PRODUCT_NOT_ADDED, Response.Status.BAD_REQUEST))
        .replaceWith(this.getByUserId(userId))
        .map(wishlist -> this.updateWishlist(wishlist, productReference))
        .call(MongoUtils::updateEntity);
  }

  private Wishlist updateWishlist(Wishlist wishlist, ProductReference productReference) {
    Optional<ProductReference> optionalProductReference = wishlist.getProducts().stream()
        .filter(p -> p._id.equalsIgnoreCase(productReference._id)).findFirst();

    if(optionalProductReference.isPresent()){
      optionalProductReference.get().setQuantity(optionalProductReference.get().getQuantity() + productReference.getQuantity());
    }
    else{
      wishlist.getProducts().add(productReference);
    }

    return wishlist;
  }

  public Uni<Wishlist> addProductToCart(String userId, ProductReference productReference) {
    return validator.validate(productReference)
        .replaceWith(productService.getById(productReference._id))
        .onFailure().transform(transformToBadRequest(WishlistException.PRODUCT_NOT_ADDED, Response.Status.BAD_REQUEST))
        .flatMap(product -> sessionWrapper.getSession().flatMap(
            session ->
                shoppingCartService.update(session, userId, productReference)
                    .replaceWith(this.removeProductFromWishlist(session, userId, productReference))
                    .flatMap(updatedWishlist -> Uni.createFrom().publisher(session.commitTransaction()).replaceWith(updatedWishlist))
                    .eventually(session::close)
        ));
  }

  public Uni<Wishlist> updateProductQuantity(String id, ProductReference productReference) {
    return repository.updateProductQuantity(id, productReference._id, productReference.getQuantity());
  }

  public Uni<Wishlist> removeProductFromWishlist(String userId, ProductReference productReference) {
    return repository.removeProductFromWishlist(userId, productReference);
  }

  public Uni<Wishlist> removeProductFromWishlist(ClientSession session, String userId, ProductReference productReference) {
    return repository.removeProductFromWishlist(session, userId, productReference);
  }

  public Uni<SuccessResponse> emptyWishlist(String userId) {
    return repository.emptyWishlist(userId)
        .onFailure().transform(transformToBadRequest(WishlistException.WISHLIST_NOT_CLEARED, Response.Status.BAD_REQUEST))
        .replaceWith(SuccessResponse.toSuccessResponse());
  }


  private Function<Throwable, Throwable> transformToBadRequest(String message, Response.Status status) {
    return throwable -> {
      if (throwable instanceof BaseException) {
        return new WishlistException(message, status);
      }
      return throwable;
    };
  }

}