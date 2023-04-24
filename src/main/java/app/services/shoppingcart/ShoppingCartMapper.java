package app.services.shoppingcart;

//import app.services.order.models.CreateOrder;
//import app.services.order.models.OrderDetails;

import app.services.order.models.CreateOrder;
import app.services.order.models.OrderDetails;
import app.services.shoppingcart.models.CreateShoppingCart;
import app.services.shoppingcart.models.ShoppingCart;

import java.util.ArrayList;

public class ShoppingCartMapper {
  public static ShoppingCart from(CreateShoppingCart createShoppingCart) {
    ShoppingCart shoppingCart = new ShoppingCart();
    shoppingCart.setUserReference(createShoppingCart.getUserReference());
    shoppingCart.setTotal(0.0);
    shoppingCart.setProducts(new ArrayList<>());
    return shoppingCart;
  }

  public static CreateOrder from(ShoppingCart shoppingCart, OrderDetails orderDetails) {
    CreateOrder createOrder = new CreateOrder();
    createOrder.setUserReference(shoppingCart.getUserReference());
    createOrder.setTotal(shoppingCart.getTotal());
    createOrder.setProducts(shoppingCart.getProducts());
    createOrder.setPaymentMethod(orderDetails.getPaymentMethod());
    createOrder.setShippingAddress(orderDetails.getShippingAddress());
    createOrder.setShipmentType(orderDetails.getShipmentType());
    return createOrder;
  }

}
