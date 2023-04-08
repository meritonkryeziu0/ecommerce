package app.services.order.models;

import app.services.product.models.ProductReference;
import app.services.accounts.models.UserReference;
import app.shared.BaseAddress;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class CreateOrder {
  private String shipmentType;
  @NotNull
  @Valid
  private BaseAddress shippingAddress;
  @Valid
  private PaymentMethod paymentMethod;
  @NotNull
  @Valid
  private UserReference userReference;
  @NotNull
  @Valid
  private List<ProductReference> products;
  @NotNull
  private Double total;
}