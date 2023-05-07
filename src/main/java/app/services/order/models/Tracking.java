package app.services.order.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tracking {
  private String carrierCompany;
  private String trackingNumber;
  private String status;
  private String estimatedDelivery;

//  public Tracking(TrackingReply trackingReply){
//    this.carrierCompany = trackingReply.getCarrierCompany();
//    this.trackingNumber = trackingReply.getTrackingNumber();
//    this.status = trackingReply.getStatus();
//    this.estimatedDelivery = trackingReply.getEstimatedDelivery();
//  }
}
