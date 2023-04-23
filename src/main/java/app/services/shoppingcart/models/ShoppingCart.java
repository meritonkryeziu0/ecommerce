package app.services.shoppingcart.models;

import app.mongodb.MongoCollections;
import app.services.accounts.models.UserReference;
import app.services.product.models.ProductReference;
import app.shared.BaseModel;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@MongoEntity(collection = MongoCollections.SHOPPING_CARTS_COLLECTION)
public class ShoppingCart extends BaseModel {
  public static final String FIELD_PRODUCTS = "products";
  public static final String FIELD_TOTAL = "total";
  public static final String FIELD_USER_ID = "userReference._id";
  private UserReference userReference;
  private List<ProductReference> products;
  private Double total;

}
