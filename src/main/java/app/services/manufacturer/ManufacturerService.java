package app.services.manufacturer;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.manufacturer.exceptions.ManufacturerException;
import app.services.manufacturer.models.CreateManufacturer;
import app.services.manufacturer.models.Manufacturer;
import app.services.manufacturer.models.UpdateManufacturer;
import app.services.product.ProductService;
import app.shared.SuccessResponse;
import app.utils.Utils;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.function.Function;

@ApplicationScoped
public class ManufacturerService {
  @Inject
  CustomValidator validator;
  @Inject
  ProductService productService;
  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;

  public ReactiveMongoCollection<Manufacturer> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.PRODUCTS_COLLECTION, Manufacturer.class);
  }

  public Uni<PaginatedResponse<Manufacturer>> getList(PaginationWrapper wrapper) {
    return MongoUtils.getPaginatedItems(getCollection(), wrapper);
  }

  public Uni<Manufacturer> getById(String id) {
    return Manufacturer.findById(id)
        .onItem().ifNull()
        .failWith(new ManufacturerException(ManufacturerException.MANUFACTURER_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(Manufacturer.class));
  }

  public Uni<Manufacturer> add(CreateManufacturer createManufacturer) {
    return validator.validate(createManufacturer)
        .replaceWith(ManufacturerMapper.INSTANCE.from(createManufacturer))
        .call(MongoUtils::addEntity);
  }

  public Uni<SuccessResponse> delete(String id) {
    return productService.getListByManufacturerId(id).onItem()
        .ifNull().failWith(new ManufacturerException(ManufacturerException.MANUFACTURER_NOT_FOUND, Response.Status.NOT_FOUND))
        .flatMap(products -> {
          if (products.size() == 0) {
            return Manufacturer.deleteById(id).replaceWith(SuccessResponse.toSuccessResponse());
          }
          return Uni.createFrom().failure(new ManufacturerException(ManufacturerException.MANUFACTURER_HAS_PRODUCTS, Response.Status.BAD_REQUEST));
        });
  }

  public Uni<Manufacturer> update(String id, UpdateManufacturer updateManufacturer) {
    return validator.validate(updateManufacturer)
        .replaceWith(this.getById(id))
        .onFailure().transform(transformToBadRequest())
        .map(ManufacturerMapper.from(updateManufacturer))
        .call(MongoUtils::updateEntity);
  }

  private Function<Throwable, Throwable> transformToBadRequest() {
    return throwable -> {
      if (throwable instanceof BaseException) {
        return new ManufacturerException(ManufacturerException.MANUFACTURER_NOT_FOUND, Response.Status.NOT_FOUND);
      }
      return throwable;
    };
  }
}