package app.services.accounts.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReference {
  @NotBlank
  public String _id;
  @NotBlank
  private String firstName;
  @NotBlank
  private String lastName;
}
