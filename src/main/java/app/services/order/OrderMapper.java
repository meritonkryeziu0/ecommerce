package app.services.order;

import app.services.order.models.CreateOrder;
import app.services.order.models.Order;
import app.services.order.models.UpdateOrder;

import java.util.function.Function;
import java.util.regex.Pattern;

public class OrderMapper {
  public static Order from(CreateOrder createOrder) {
    Order order = new Order();
    order.setUserReference(createOrder.getUserReference());
    order.setProducts(createOrder.getProducts());
    order.setShippingAddress(createOrder.getShippingAddress());
    order.setPaymentMethod(createOrder.getPaymentMethod());
    order.setTotal(createOrder.getTotal());
    order.setStatus("open-order");
    order.setShipmentType(createOrder.getShipmentType());
    if (!Pattern.compile("standard|express").matcher(order.getShipmentType()).matches()) {
      order.setShipmentType("standard");
    }
    return order;
  }

  public static Function<Order, Order> from(UpdateOrder updateOrder) {
    return order -> {
      order.setShippingAddress(updateOrder.getShippingAddress());
      return order;
    };
  }
}