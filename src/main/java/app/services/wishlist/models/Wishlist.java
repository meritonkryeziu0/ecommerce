package app.services.wishlist.models;

import app.mongodb.MongoCollections;
import app.shared.BaseModel;
import app.services.accounts.models.UserReference;
import app.services.product.models.ProductReference;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@MongoEntity(collection = MongoCollections.WISHLIST_COLLECTION)
public class Wishlist extends BaseModel {
  public static String FIELD_USER_ID = "userReference._id";
  public static String FIELD_PRODUCTS = "products";
  public static String FIELD_PRODUCT_ID = "products._id";

  private UserReference userReference;
  private List<ProductReference> products;
}