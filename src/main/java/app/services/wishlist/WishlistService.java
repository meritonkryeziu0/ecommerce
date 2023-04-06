package app.services.wishlist;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.services.product.ProductService;
import app.services.product.exceptions.ProductException;
import app.services.product.models.ProductReference;
import app.services.wishlist.exceptions.WishlistException;
import app.services.wishlist.models.Wishlist;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Function;

@ApplicationScoped
public class WishlistService {
  @Inject
  ProductService productService;
  @Inject
  WishlistRepository repository;

  @Inject
  CustomValidator validator;

  public Uni<Wishlist> getByUserId(String userId) {
    return repository.getByUserId(userId);
  }

  public Uni<Wishlist> update(String userId, ProductReference productReference) {
    return validator.validate(productReference)
        .replaceWith(productService.getById(productReference._id))
        .onFailure().transform(transformToBadRequest())
        .flatMap(product -> {
          if (productReference.getQuantity() > product.getStockQuantity()) {
            return Uni.createFrom().failure(new ProductException(ProductException.NOT_ENOUGH_STOCK, 400));
          }
          return Uni.createFrom().item(product);
        })
        .onFailure().transform(transformToBadRequest())
        .replaceWith(this.getByUserId(userId))
        .map(wishlist -> this.updateWishlist(wishlist, productReference))
        .flatMap(wishlist -> repository.update(userId, wishlist));
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

  public Uni<SuccessResponse> emptyWishlist(String userId) {
    return repository.emptyWishlist(userId)
        .onFailure().transform(transformToBadRequest())
        .replaceWith(SuccessResponse.toSuccessResponse());
  }


  private Function<Throwable, Throwable> transformToBadRequest() {
    return throwable -> {
      if (throwable instanceof BaseException) {
        return new WishlistException(WishlistException.PRODUCT_NOT_ADDED, 400);
      }
      return throwable;
    };
  }
}
