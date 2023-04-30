package app.services.product;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.manufacturer.models.ManufacturerReference;
import app.services.product.models.Product;
import app.services.product.models.ProductReference;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductRepository {
  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;

  public ReactiveMongoCollection<Product> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.PRODUCTS_COLLECTION, Product.class);
  }

  public Uni<List<Product>> getListByManufacturerId(String id) {
    return getCollection().find(Filters.eq(Product.FIELD_MANUFACTURER_ID, id)).collect().asList();
  }

  public Uni<UpdateResult> updateManufactures(ManufacturerReference manufacturerReference) {
    return getCollection().updateMany(Filters.eq(Product.FIELD_MANUFACTURER_ID, manufacturerReference.getId()), Updates.set(Product.FIELD_MANUFACTURER, manufacturerReference));
  }

  public Uni<Void> increaseStockQuantity(ClientSession session, List<ProductReference> productReferences) {
    List<UpdateOneModel<Product>> updates = productReferences.stream().map(productReference -> new UpdateOneModel<Product>(
            Filters.eq(Product.FIELD_ID, productReference.id),
            Updates.inc(Product.FIELD_STOCK_QUANTITY, productReference.getQuantity()))).collect(Collectors.toList());

    return getCollection().bulkWrite(session, updates).replaceWithVoid();
  }

  public Uni<Void> decreaseStockQuantity(ClientSession session, List<ProductReference> productReferences) {
    List<UpdateOneModel<Product>> updates = productReferences.stream().map(productReference -> new UpdateOneModel<Product>(
            Filters.eq(Product.FIELD_ID, productReference.id),
            Updates.inc(Product.FIELD_STOCK_QUANTITY, -productReference.getQuantity()))).collect(Collectors.toList());

    return getCollection().bulkWrite(session, updates).replaceWithVoid();
  }

  public Uni<Product> add(ClientSession session, Product product){
    return MongoUtils.addEntity(session, getCollection(), product);
  }

  public Uni<Product> update(ClientSession session, Product product){
    return MongoUtils.updateEntity(session, getCollection(), Filters.eq(Product.FIELD_ID, product.getId()), product);
  }

  public Uni<Void> delete(ClientSession session, String id) {
    return getCollection().findOneAndDelete(session, Filters.eq(Product.FIELD_ID, id))
            .replaceWithVoid();
  }
}