package app.helpers;

import app.mongodb.MongoCollectionWrapper;
import app.services.product.models.Product;
import app.services.product.models.ProductReference;
import app.services.shoppingcart.models.ShoppingCart;
import app.shared.BaseModel;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.mutiny.Uni;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;

@ApplicationScoped
public class ProductUpdateHelper {
  @Inject
  MongoCollectionWrapper mongoClient;

  public <T extends BaseModel> Uni<Void> updateProductOccurrences(ClientSession session, String collectionName, Class<T> clazz, ProductReference productReference) {
    return mongoClient.getCollection(collectionName, clazz).aggregate(session, Arrays.asList(new Document("$set",
            new Document(ShoppingCart.FIELD_PRODUCTS,
                new Document("$map",
                    new Document("input", "$" + ShoppingCart.FIELD_PRODUCTS)
                        .append("as", "product")
                        .append("in",
                            new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$$product._id", productReference.id)),
                                new Document(BaseModel.FIELD_ID, "$$product._id")
                                    .append(Product.FIELD_NAME, productReference.getName())
                                    .append(Product.FIELD_PRICE, productReference.getPrice())
                                    .append(ProductReference.FIELD_QUANTITY, "$$product." + ProductReference.FIELD_QUANTITY), "$$product")))))),
        new Document("$set",
            new Document(ShoppingCart.FIELD_TOTAL,
                new Document("$sum",
                    new Document("$map",
                        new Document("input", "$" + ShoppingCart.FIELD_PRODUCTS)
                            .append("as", "product")
                            .append("in",
                                new Document("$multiply", Arrays.asList("$$product." + Product.FIELD_PRICE, "$$product." + ProductReference.FIELD_QUANTITY))))))),
        new Document("$merge",
            new Document("into", collectionName)
                .append("on", BaseModel.FIELD_ID)
                .append("whenMatched", "replace")
                .append("whenNotMatched", "discard")))).collect().asList().replaceWithVoid();
  }
}
