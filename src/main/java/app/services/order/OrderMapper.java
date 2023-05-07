package app.services.order;

import app.proto.EditShippingAddressRequest;
import app.proto.NotifyUserRequest;
import app.services.order.models.CreateOrder;
import app.services.order.models.Order;
import app.services.order.models.UpdateOrder;
import app.shared.BaseAddress;
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

  public static Function<Order, Order> from(UpdateOrder updateOrder) {
    return order -> {
      order.setShippingAddress(updateOrder.getShippingAddress());
      return order;
    };
  }

  public static EditShippingAddressRequest toEditShippingAddressRequest(String trackingNumber, BaseAddress shippingAddress){
    EditShippingAddressRequest.Builder builder = EditShippingAddressRequest.newBuilder();
    if(trackingNumber != null){
      builder.setTrackingNumber(trackingNumber);
    }
    if(!(shippingAddress.getZip() == null || shippingAddress.getCity() == null
        || shippingAddress.getStreet() == null)){
      builder.setShippingAddress(app.proto.ShippingAddress.newBuilder().setCity(shippingAddress.getCity())
          .setZip(shippingAddress.getZip()).setStreet(shippingAddress.getStreet()).build());
    }
    return builder.build();
  }

  public static NotifyUserRequest toNotifyUserRequest(Order order){
    NotifyUserRequest.Builder builder = NotifyUserRequest.newBuilder();

    if(order.getUserReference() != null && order.getUserReference().getFirstName() != null && order.getUserReference().getLastName() != null && order.getUserReference().getEmail() != null){
      builder.setUserReference(NotifyUserRequest.UserReference.newBuilder()
          .setFirstName(order.getUserReference().getFirstName())
          .setLastName(order.getUserReference().getLastName())
          .setLastName(order.getUserReference().getEmail()).build());
    }

    if(order.getTracking().getTrackingNumber() != null){
      builder.setOrderNumber(order.getTracking().getTrackingNumber());
    }

    if(order.getStatus() != null) {
      builder.setStatus(order.getStatus());
    }
    return builder.build();
  }

}