package app.services.seller;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.product.models.Product;
import app.services.seller.models.SellerProduct;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static app.common.CommonMethods.sellerProducts;

@ApplicationScoped
public class SellerProductRepository {
  @Inject
  MongoCollectionWrapper mongoClient;

  public ReactiveMongoCollection<SellerProduct> getCollection() {
    return mongoClient.getCollection(MongoCollections.SELLER_PRODUCTS_COLLECTION, SellerProduct.class);
  }

  public Uni<List<Product>> getSellersProducts(String sellerId){
    List<Document> pipeline = sellerProducts(sellerId);
    return getCollection().aggregate(pipeline, Product.class).collect().asList();
  }

  public Uni<SellerProduct> add(ClientSession session, SellerProduct sellerProduct) {
    return MongoUtils.addEntity(session, getCollection(), sellerProduct);
  }

  public Uni<SellerProduct> update(ClientSession session, SellerProduct sellerProduct) {
    return MongoUtils.updateEntity(session, getCollection(), Filters.eq(SellerProduct.FIELD_ID, sellerProduct.getId()), sellerProduct);
  }

  public Uni<Void> delete(ClientSession session, String id) {
    return getCollection().findOneAndDelete(session, Filters.eq(SellerProduct.FIELD_ID, id))
        .replaceWithVoid();
  }
}