
package app.services.manufacturer;

import app.exceptions.BaseException;
import app.common.CustomValidator;
import app.mongodb.MongoUtils;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.services.manufacturer.models.CreateManufacturer;
import app.services.manufacturer.models.Manufacturer;
import app.services.manufacturer.models.UpdateManufacturer;
import app.services.product.ProductService;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Function;

@ApplicationScoped
public class ManufacturerService {
    @Inject
    CustomValidator validator;
    @Inject
    ManufacturerRepository repository;
    @Inject
    ProductService productService;
    @Inject
    MongoSessionWrapper sessionWrapper;

//    public Uni<PaginatedResponse<Manufacturer>> getList(PaginationWrapper wrapper) {
//        return MongoUtils.getPaginatedItems(repository.getCollection(), wrapper);
//    }

    public Uni<Manufacturer> getById(String id) {
        return repository.getById(id).onItem().ifNull().failWith(new ManufacturerException(ManufacturerException.MANUFACUTURE_NOT_FOUND, 404));
    }

    public Uni<Manufacturer> add(CreateManufacturer createManufacturer) {
        return validator.validate(createManufacturer)
                .replaceWith(ManufacturerMapper.from(createManufacturer))
                .flatMap(manufacture -> repository.add(manufacture));
    }

//    public Uni<SuccessResponse> delete(String id) {
//        return productService.getListByManufacturerId(id).onItem()
//                .ifNull().failWith(new ManufacturerException(ManufacturerException.MANUFACUTURE_NOT_FOUND, 400))
//                .flatMap(products -> {
//                    if (products.size() == 0) {
//                        return repository.delete(id).replaceWith(SuccessResponse.toSuccessResponse());
//                    }
//                    return Uni.createFrom().failure(new ManufacturerException(ManufacturerException.MANUFACUTURE_HAS_PRODUCTS, 400));
//                });
//    }

    public Uni<Manufacturer> update(String id, UpdateManufacturer updateManufacturer) {
        return validator.validate(updateManufacturer)
                .replaceWith(this.getById(id))
                .onFailure().transform(transformToBadRequest())
                .map(ManufacturerMapper.from(updateManufacturer))
                .flatMap(repository.);
    }

    private Function<Throwable, Throwable> transformToBadRequest() {
        return throwable -> {
            if (throwable instanceof BaseException) {
                return new ManufacturerException(ManufacturerException.MANUFACUTURE_NOT_FOUND, 404);
            }
            return throwable;
        };
    }
}