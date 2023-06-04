package app.services.seller.models;

import app.mongodb.MongoCollections;
import app.services.accounts.models.UserReference;
import app.services.product.models.ProductReference;
import app.shared.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MongoEntity(collection = MongoCollections.SELLER_PRODUCTS_COLLECTION)
public class SellerProduct extends BaseModel {
  public static final String FIELD_USER_REFERENCE = "userReference";
  public static final String FIELD_USER_REFERENCE_ID = "userReference._id";
  public static final String FIELD_PRODUCT_REFERENCE = "productReference";
  public static final String FIELD_PRODUCT_REFERENCE_ID = "productReference._id";


  private UserReference userReference;
  private ProductReference productReference;
  private Boolean isPromoted;

  @JsonIgnore
  public String getUserId(){
    return getUserReference().getId();
  }
  @JsonIgnore
  public String getProductReferenceId(){
    return productReference.getId();
  }
}