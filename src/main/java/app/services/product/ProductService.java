package app.services.product;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.manufacturer.ManufacturerService;
import app.services.product.exceptions.ProductException;
import app.services.product.models.CreateProduct;
import app.services.product.models.Product;
import app.services.product.models.ProductReference;
import app.services.product.models.UpdateProduct;
import app.utils.Utils;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class ProductService {
  @Inject
  CustomValidator validator;
  @Inject
  ManufacturerService manufactureService;
  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;

  @Inject
  ProductRepository repository;

  public ReactiveMongoCollection<Product> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.PRODUCTS_COLLECTION, Product.class);
  }

  public Uni<Product> getById(String id) {
    return Product.findById(id)
        .onItem().ifNull()
        .failWith(new ProductException(ProductException.PRODUCT_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(Product.class));
  }

  public Uni<PaginatedResponse<Product>> getList(PaginationWrapper wrapper) {
    return MongoUtils.getPaginatedItems(getCollection(), wrapper);
  }

  public Uni<PaginatedResponse<Product>> getListByCategory(String mainCategory, String subcategory, ProductFilterWrapper wrapper) {
    return MongoUtils.getPaginatedItemsFromList(repository.getListByCategory(mainCategory, subcategory), wrapper);
  }

  public Uni<List<Product>> getListByManufacturerId(String id) {
    return Product.find(Product.FIELD_MANUFACTURER_ID, id).list();
  }

  public Uni<List<Product>> getListByCategoryId(String id){
    return Product.find(Product.FIELD_CATEGORY_ID, id).list();
  }

  public Uni<Product> add(CreateProduct createProduct) {
    return validator.validate(createProduct)
        .replaceWith(manufactureService.getById(createProduct.getManufacturer().getId()))
        .replaceWith(ProductMapper.INSTANCE.from(createProduct))
        .call(MongoUtils::addEntity);
  }

  public Uni<Product> add(ClientSession session, CreateProduct createProduct) {
    return validator.validate(createProduct)
        .replaceWith(manufactureService.getById(createProduct.getManufacturer().getId()))
        .replaceWith(ProductMapper.INSTANCE.from(createProduct))
        .call(product -> repository.add(session, product));
  }

  public Uni<Product> update(String id, UpdateProduct updateProduct) {
    return validator.validate(updateProduct)
        .replaceWith(this.getById(id))
        .onFailure().transform(transformToBadRequest(ProductException.PRODUCT_NOT_FOUND))
        .map(ProductMapper.from(updateProduct))
        .call(MongoUtils::updateEntity);
  }

  public Uni<Product> update(ClientSession session, String id, UpdateProduct updateProduct) {
    return validator.validate(updateProduct)
        .replaceWith(this.getById(id))
        .onFailure().transform(transformToBadRequest(ProductException.PRODUCT_NOT_FOUND))
        .map(ProductMapper.from(updateProduct))
        .call(product -> repository.update(session, product));
  }

  public Uni<Void> delete(String id) {
    return Product.deleteById(id).replaceWithVoid();
  }

  public Uni<Void> delete(ClientSession session, String id) {
    return repository.delete(session, id);
  }

  public Uni<Void> increaseQuantity(ClientSession session, List<ProductReference> productReference) {
    return repository.updateStockQuantity(session, true, productReference);
  }

  private Function<Throwable, Throwable> transformToBadRequest(String message) {
    return throwable -> {
      if (throwable instanceof BaseException) {
        return new ProductException(message, Response.Status.BAD_REQUEST);
      }
      return throwable;
    };
  }

}