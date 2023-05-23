package app.services.order.models;

import app.shared.BaseAddress;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderDetails {
  @Valid
  private BaseAddress shippingAddress;
  @NotNull
  private Order.PaymentType paymentType;
  @Valid
  private CardDetails cardDetails;
  @NotNull
  private String shipmentType;
}