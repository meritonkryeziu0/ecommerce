package app.services.accounts.models;

import app.shared.BaseAddress;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingAddress extends BaseAddress {
  public String id;
}
