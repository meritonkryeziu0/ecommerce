package app.services.accounts.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseCreateUser {
  @NotBlank
  @Size(max = 255)
  private String firstName;
  @NotBlank
  @Size(max = 255)
  private String lastName;
  @NotBlank
  @Email
  private String email;
  @NotBlank
  private String password;
  @NotBlank
  private String phoneNumber;
  private List<ShippingAddress> shippingAddresses;
  private BillingAddress billingAddress;
}
