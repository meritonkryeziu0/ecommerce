package app.services.promotion;

import app.mongodb.MongoCollections;
import app.services.seller.models.SellerProduct;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MongoEntity(collection = MongoCollections.PROMOTED_PRODUCTS_COLLECTION)
public class PromotedProduct extends SellerProduct {
  private int feePercentage;
}