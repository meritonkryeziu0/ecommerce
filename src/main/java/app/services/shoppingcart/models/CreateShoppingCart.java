package app.services.shoppingcart.models;

import app.services.accounts.models.UserReference;
import app.services.product.models.ProductReference;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class CreateShoppingCart {
  @Valid
  List<ProductReference> products;
  private UserReference userReference;
  private Double total;

}
