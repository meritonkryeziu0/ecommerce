package app.services.order;

import app.services.order.models.CreateOrder;
import app.services.order.models.Order;
import app.services.order.models.UpdateOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.function.Function;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
  OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

  @Mapping(target = "status", constant = "open-order")
  @Mapping(target = "shipmentType", expression = "java(!java.util.regex.Pattern.compile(\"standard|express\").matcher(createOrder.getShipmentType()).matches() ? \"standard\" : createOrder.getShipmentType())")
  Order from(CreateOrder createOrder);

  static Function<Order, Order> from(UpdateOrder updateOrder) {
    return order -> {
      order.setShippingAddress(updateOrder.getShippingAddress());
      return order;
    };
  }

}