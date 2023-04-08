package app.services.wishlist.models;

import app.shared.BaseModel;

import app.services.accounts.models.UserReference;
import app.services.product.models.ProductReference;
import app.shared.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Wishlist extends BaseModel {
  public static String FIELD_USER_ID = "userReference._id";
  public static String FIELD_PRODUCTS = "products";
  public static String FIELD_PRODUCT_ID = "products._id";

  private UserReference userReference;
  private List<ProductReference> products;
}