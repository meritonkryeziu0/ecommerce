package app.services.shoppingCart;

import app.exceptions.CustomValidator;
import app.mongodb.MongoUtils;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.services.product.ProductService;
import app.services.product.models.ProductReference;
import app.services.shoppingCart.models.CreateShoppingCart;
import app.services.shoppingCart.models.ShoppingCart;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

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

}
