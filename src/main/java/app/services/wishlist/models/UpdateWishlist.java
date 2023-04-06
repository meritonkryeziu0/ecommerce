package app.services.wishlist.models;

import app.services.product.models.ProductReference;

import javax.validation.Valid;
import java.util.List;

public class UpdateWishlist {
  @Valid
  private List<ProductReference> products;
}
