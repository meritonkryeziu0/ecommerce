package app.services.shoppingcart;

import app.services.order.models.CreateOrder;
import app.services.order.models.Order;
import app.services.order.models.OrderDetails;
import app.services.shoppingcart.models.CreateShoppingCart;
import app.services.shoppingcart.models.ShoppingCart;
import app.utils.Utils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShoppingCartMapper {
  ShoppingCartMapper INSTANCE = Mappers.getMapper(ShoppingCartMapper.class);

  @Mapping(target = "total", constant = "0.0")
  @Mapping(target = "products", expression = "java(new java.util.ArrayList<>())")
  ShoppingCart from(CreateShoppingCart createShoppingCart);

  static CreateOrder from(ShoppingCart shoppingCart, OrderDetails orderDetails) {
    CreateOrder createOrder = new CreateOrder();
    createOrder.setUserReference(shoppingCart.getUserReference());
    createOrder.setTotal(shoppingCart.getTotal());
    createOrder.setProducts(shoppingCart.getProducts());
    createOrder.setPaymentType(orderDetails.getPaymentType());

    if(orderDetails.getPaymentType().equals(Order.PaymentType.CARD)) {
      createOrder.setCardDetails(orderDetails.getCardDetails());
    }

    createOrder.setShippingAddress(orderDetails.getShippingAddress());
    createOrder.setShipmentType(orderDetails.getShipmentType());
    return createOrder;
  }

}
