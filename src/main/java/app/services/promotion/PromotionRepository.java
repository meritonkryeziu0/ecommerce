package app.services.promotion;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.services.product.models.Product;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static app.common.CommonMethods.sellerProducts;
import static app.common.CommonMethods.promotedProducts;

@ApplicationScoped
public class PromotionRepository {

  @Inject
  MongoCollectionWrapper mongoClient;

  public ReactiveMongoCollection<PromotedProduct> getCollection() {
    return mongoClient.getCollection(MongoCollections.PROMOTED_PRODUCTS_COLLECTION, PromotedProduct.class);
  }

  public Uni<List<Product>> getPromotedProducts() {
    List<Document> pipeline = promotedProducts();
    return getCollection().aggregate(pipeline, Product.class).collect().asList();
  }

  public Uni<List<Product>> getPromotedProducts(String sellerId){
    List<Document> pipeline = sellerProducts(sellerId);
    return getCollection().aggregate(pipeline, Product.class).collect().asList();
  }

  public Uni<Void> delete(ClientSession session, String productId) {
    return getCollection().findOneAndDelete(session, Filters.eq(PromotedProduct.FIELD_PRODUCT_REFERENCE_ID, productId))
        .replaceWithVoid();
  }

}
