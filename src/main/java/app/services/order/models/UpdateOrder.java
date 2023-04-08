package app.services.order.models;

import app.services.accounts.models.ShippingAddress;
import app.services.accounts.models.UserReference;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class UpdateOrder {
  @Valid
  private UserReference user;
  @Valid
  private ShippingAddress shippingAddress;
}