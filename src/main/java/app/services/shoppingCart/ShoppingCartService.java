package app.services.shoppingCart;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.mongodb.MongoUtils;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.services.product.ProductService;
import app.services.product.exceptions.ProductException;
import app.services.product.models.ProductReference;
import app.services.shoppingCart.exceptions.ShoppingCartException;
import app.services.shoppingCart.models.CreateShoppingCart;
import app.services.shoppingCart.models.ShoppingCart;
import app.utils.Utils;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.common.constraint.Nullable;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Function;

@ApplicationScoped
public class ShoppingCartService {
    @Inject
    CustomValidator validator;

    @Inject
    ShoppingCartRepository repository;

    @Inject
    ProductService productService;

    public Uni<PaginatedResponse<ShoppingCart>> getList(PaginationWrapper wrapper) {
        return MongoUtils.getPaginatedItems(repository.getCollection(), wrapper);
    }

    public Uni<ShoppingCart> getByUserId(String userId) {
        return repository.getByUserId(userId)
            .onItem().ifNull().failWith(new ShoppingCartException(ShoppingCartException.SHOPPING_CART_NOT_FOUND, 404));
    }

    public Uni<ShoppingCart> add(ClientSession session, CreateShoppingCart createShoppingCart) {
        return validator.validate(createShoppingCart)
                .replaceWith(ShoppingCartMapper.from(createShoppingCart))
                .flatMap(shoppingCart -> repository.add(session, shoppingCart));
    }

    private ShoppingCart updateShoppingCart(ShoppingCart shoppingCart, ProductReference productReference) {
        Optional<ProductReference> optionalProductReference = shoppingCart.getProducts().stream()
                .filter(p -> p._id.equals(productReference._id)).findFirst();
        if (optionalProductReference.isPresent()) {
            optionalProductReference.get().setQuantity(optionalProductReference.get().getQuantity() + productReference.getQuantity());
        } else {
            shoppingCart.getProducts().add(productReference);
        }
        shoppingCart.setTotal(shoppingCart.getTotal() + productReference.getQuantity() * productReference.getPrice());
        return shoppingCart;
    }

    public Uni<ShoppingCart> update(@Nullable ClientSession session, String userId, ProductReference productReference) {
        return validator.validate(productReference)
            .replaceWith(productService.getById(productReference._id))
            .onFailure().transform(transformToBadRequest())
            .flatMap(product -> {
                if (productReference.getQuantity() > product.getStockQuantity()) {
                    return Uni.createFrom().failure(new ProductException(ProductException.NOT_ENOUGH_STOCK, 400));
                }
                return Uni.createFrom().item(product);
            })
            .replaceWith(this.getByUserId(userId))
            .onFailure().transform(transformToBadRequest())
            .map(shoppingCart -> this.updateShoppingCart(shoppingCart, productReference))
            .flatMap(shoppingCart -> {
                if(Utils.isNull(session)){
                    return repository.update(userId, shoppingCart);
                }
                return repository.update(session, userId, shoppingCart);
            });
    }

    private Function<Throwable, Throwable> transformToBadRequest() {
        return throwable -> {
            if (throwable instanceof BaseException) {
                return new ShoppingCartException(ShoppingCartException.PRODUCT_NOT_ADDED, 400);
            }
            return throwable;
        };
    }


}
