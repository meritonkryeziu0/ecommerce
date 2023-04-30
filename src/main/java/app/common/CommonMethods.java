package app.common;

import app.services.seller.models.SellerProduct;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class CommonMethods {

  public static List<Document> sellerProducts(String sellerId) {
    return List.of(new Document("$match",
            new Document(SellerProduct.FIELD_USER_REFERENCE_ID, sellerId)),
        new Document("$lookup",
            new Document("from", "products")
                .append("localField", "productReferenceId")
                .append("foreignField", "_id")
                .append("as", "product")),
        new Document("$unwind",
            new Document("path", "$product")),
        new Document("$replaceRoot",
            new Document("newRoot", "$product")));
  }

  public static List<Document> promotedProducts() {
    return Arrays.asList(
        new Document("$lookup",
            new Document("from", "products")
                .append("localField", "productReferenceId")
                .append("foreignField", "_id")
                .append("as", "product")),
        new Document("$unwind",
            new Document("path", "$product")),
        new Document("$replaceRoot",
            new Document("newRoot", "$product")));
  }

}
