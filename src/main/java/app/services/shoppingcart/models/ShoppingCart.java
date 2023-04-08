package app.services.shoppingcart.models;

import app.services.accounts.models.UserReference;
import app.services.product.models.ProductReference;
import app.shared.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShoppingCart extends BaseModel {
  public static final String FIELD_PRODUCTS = "products";
  public static final String FIELD_TOTAL = "total";
  public static final String FIELD_USER_ID = "userReference._id";
  private UserReference userReference;
  private List<ProductReference> products;
  private Double total;

}
