package app.services.shoppingcart.models;

import app.services.product.models.ProductReference;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class UpdateShoppingCart {
  @Valid
  private List<ProductReference> products;
}
