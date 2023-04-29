package app.services.accounts.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateUser extends BaseCreateUser{
  @NotBlank
  private String role;
}
