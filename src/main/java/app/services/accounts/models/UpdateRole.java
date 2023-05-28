package app.services.accounts.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRole {
  @Pattern(regexp = "Admin|User|Seller")
  private String role;
}
