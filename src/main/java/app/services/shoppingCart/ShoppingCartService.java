package app.services.shoppingCart;

import app.exceptions.BaseException;
import app.exceptions.CustomValidator;
import app.mongodb.MongoUtils;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
//import app.helpers.ProductUpdateHelper;
import app.mongodb.MongoCollections;
//import app.services.product.ProductException;
//import app.mongodb.MongoSessionWrapper;
//import app.services.order.OrderService;
//import app.services.order.models.CreateOrder;
//import app.services.order.models.OrderDetails;
import app.services.product.ProductService;
import app.services.product.models.ProductReference;
import app.services.shoppingcart.models.CreateShoppingCart;
import app.services.shoppingcart.models.ShoppingCart;
import app.shared.SuccessResponse;
import com.mongodb.reactivestreams.client.ClientSession;
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

//    @Inject
//    OrderService orderService;
//
//    @Inject
//    ProductUpdateHelper productUpdateHelper;
//
//    @Inject
//    MongoSessionWrapper sessionWrapper;

//    public Uni<ShoppingCart> getById(String id) {
//        return repository.getById(id)
//                .onItem().ifNull().failWith(new ShoppingCartException(ShoppingCartException.SHOPPING_CART_NOT_FOUND, 404));
//    }
//
//    public Uni<ShoppingCart> getByUserId(String userId) {
//        return repository.getByUserId(userId)
//                .onItem().ifNull().failWith(new ShoppingCartException(ShoppingCartException.SHOPPING_CART_NOT_FOUND, 404));
//    }
//
//    public Uni<PaginatedResponse<ShoppingCart>> getList(PaginationWrapper wrapper) {
//        return MongoUtils.getPaginatedItems(repository.getCollection(), wrapper);
//    }

//    public Uni<ShoppingCart> add(ClientSession session, CreateShoppingCart createShoppingCart) {
//        return validator.validate(createShoppingCart)
//                .replaceWith(ShoppingCartMapper.from(createShoppingCart))
//                .flatMap(shoppingCart -> repository.add(session, shoppingCart));
//    }
//
//    //  @Scheduled(every = "50s")
//    public Uni<Void> clearNotUpdatedCarts() {
//        return repository.clearNotUpdatedCarts();
//    }

//    public Uni<ShoppingCart> update(String userId, ProductReference productReference) {
//        return validator.validate(productReference)
//                .replaceWith(productService.getById(productReference._id))
//                .onFailure().transform(transformToBadRequest())
//                .flatMap(product -> {
//                    if (productReference.getQuantity() > product.getStockQuantity()) {
//                        return Uni.createFrom().failure(new ProductException(ProductException.NOT_ENOUGH_STOCK, 400));
//                    }
//                    return Uni.createFrom().item(product);
//                })
//                .replaceWith(this.getByUserId(userId))
//                .onFailure().transform(transformToBadRequest())
//                .map(shoppingCart -> this.updateShoppingCart(shoppingCart, productReference))
//                .flatMap(shoppingCart -> repository.update(userId, shoppingCart));
//    }

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

//    public Uni<Void> updateAllCarts(ClientSession session, ProductReference product) {
//        return productUpdateHelper.updateProductOccurrences(session, MongoCollections.SHOPPING_CARTS_COLLECTION, ShoppingCart.class, product);
//    }

//    public Uni<SuccessResponse> emptyShoppingCart(String userId) {
//        return repository.emptyShoppingCart(userId)
//                .onFailure().transform(transformToBadRequest())
//                .replaceWith(SuccessResponse.toSuccessResponse());
//    }

//    public Uni<ShoppingCart> buyShoppingCart(String userId, OrderDetails orderDetails) {
//        return repository.getByUserId(userId).onFailure().transform(transformToBadRequest())
//                .flatMap(shoppingCart -> {
//                    CreateOrder createOrder = ShoppingCartMapper.from(shoppingCart, orderDetails);
//                    return Uni.createFrom().item(createOrder)
//                            .flatMap(createOrder1 -> sessionWrapper.getSession()
//                                    .flatMap(
//                                            session -> orderService.add(session, createOrder1)
//                                                    .replaceWith(this.emptyShoppingCart(userId))
//                                                    .replaceWith(Uni.createFrom().publisher(session.commitTransaction()))
//                                                    .replaceWith(shoppingCart)
//                                                    .eventually(session::close)
//                                    ));
//                });
//    }

//    private Function<Throwable, Throwable> transformToBadRequest() {
//        return throwable -> {
//            if (throwable instanceof BaseException) {
//                return new ShoppingCartException(ShoppingCartException.PRODUCT_NOT_ADDED, 400);
//            }
//            return throwable;
//        };
//    }
}
