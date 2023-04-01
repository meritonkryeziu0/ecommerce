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
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class ProductService {
    @Inject
    CustomValidator validator;
    @Inject
    ProductRepository repository;
    @Inject
    ManufacturerService manufactureService;

    public Uni<Product> getById(String id) {
        return repository.getById(id).onItem().ifNull().failWith(new ProductException(ProductException.PRODUCT_NOT_FOUND, 404));
    }

    public Uni<PaginatedResponse<Product>> getList(PaginationWrapper wrapper) {
        return MongoUtils.getPaginatedItems(repository.getCollection(), wrapper);
    }

    public Uni<List<Product>> getListByManufacturerId(String id) {
        return repository.getListByManufacturerId(id);
    }

    public Uni<Product> add(CreateProduct createProduct) {
        return validator.validate(createProduct)
                .replaceWith(manufactureService.getById(createProduct.getManufacturer().getId()))
                .replaceWith(ProductMapper.from(createProduct))
                .flatMap(product -> repository.add(product));
    }

    public Uni<Product> update(String id, UpdateProduct updateProduct) {
        return validator.validate(updateProduct)
                .replaceWith(this.getById(id))
                .onFailure().transform(transformToBadRequest(ProductException.PRODUCT_NOT_FOUND))
                .map(ProductMapper.from(updateProduct))
                .flatMap(product -> repository.update(id, product));
    }

    public Uni<Void> delete(String id) {
        return repository.delete(id).replaceWithVoid();
    }

    private Function<Throwable, Throwable> transformToBadRequest(String message) {
        return throwable -> {
            if (throwable instanceof BaseException) {
                return new ProductException(message, 400);
            }
            return throwable;
        };
    }
}
