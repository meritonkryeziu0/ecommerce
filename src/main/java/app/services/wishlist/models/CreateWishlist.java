package app.services.wishlist.models;

import app.services.accounts.models.User;
import app.services.accounts.models.UserReference;
import app.services.product.models.ProductReference;

import javax.validation.Valid;
import java.util.List;

public class CreateWishlist {
  private UserReference userReference;
  @Valid
  private List<ProductReference> products;

  public CreateWishlist(User user){
    this.userReference = new UserReference(userReference._id, user.getFirstName(), user.getLastName());
  }
}
