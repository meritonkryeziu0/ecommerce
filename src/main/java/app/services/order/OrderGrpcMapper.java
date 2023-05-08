package app.services.order;

import app.proto.EditShippingAddressRequest;
import app.proto.NotifyUserRequest;
import app.services.order.models.Order;
import app.shared.BaseAddress;

public class OrderGrpcMapper {
  static EditShippingAddressRequest toEditShippingAddressRequest(String trackingNumber, BaseAddress shippingAddress){
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

  static NotifyUserRequest toNotifyUserRequest(Order order){
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
