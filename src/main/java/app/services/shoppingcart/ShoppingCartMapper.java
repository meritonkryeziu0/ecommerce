package app.services.shoppingcart;

import app.services.shoppingcart.models.CreateShoppingCart;
import app.services.shoppingcart.models.ShoppingCart;
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
