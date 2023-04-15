package app.services.product;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoUtils;
import app.services.manufacturer.ManufacturerService;
import app.services.product.exceptions.ProductException;
import app.services.product.models.CreateProduct;
import app.services.product.models.Product;
import app.services.product.models.UpdateProduct;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import org.bson.Document;

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

  public Uni<Product> getById(String id) {
    return Product.findById(id)
        .onItem().ifNull()
        .failWith(new ProductException(ProductException.PRODUCT_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(ProductMapper.mapToProduct());
  }

  public Uni<List<Product>> getList(PaginationWrapper wrapper) {
    return Product.listAll();
  }

  public Uni<List<Product>> getListByManufacturerId(String id) {
    return Product.list((Document) Filters.eq(Product.FIELD_MANUFACTURER_ID, id));
  }

  public Uni<Product> add(CreateProduct createProduct) {
    return validator.validate(createProduct)
        .replaceWith(manufactureService.getById(createProduct.getManufacturer().get_id()))
        .replaceWith(ProductMapper.from(createProduct))
        .call(MongoUtils::addEntity);
  }

  public Uni<Product> update(String id, UpdateProduct updateProduct) {
    return validator.validate(updateProduct)
        .replaceWith(this.getById(id))
        .onFailure().transform(transformToBadRequest(ProductException.PRODUCT_NOT_FOUND))
        .map(ProductMapper.from(updateProduct))
        .call(MongoUtils::updateEntity);
  }

  public Uni<Void> delete(String id) {
    return Product.deleteById(id).replaceWithVoid();
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
