package app.services.shoppingCart;

//import app.services.order.models.CreateOrder;
//import app.services.order.models.OrderDetails;
import app.services.shoppingCart.models.CreateShoppingCart;
import app.services.shoppingCart.models.ShoppingCart;

import java.util.ArrayList;

public class ShoppingCartMapper {
    public static ShoppingCart from(CreateShoppingCart createShoppingCart) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserReference(createShoppingCart.getUserReference());
        shoppingCart.setTotal(0.0);
        shoppingCart.setProducts(new ArrayList<>());
        return shoppingCart;
    }

}
