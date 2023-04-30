package app.services.roles.models;

import app.services.authorization.ability.Ability;
import io.smallrye.common.constraint.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleWithAbilityWrapper {
  @NotNull
  private String role;
  @Valid
  private List<Ability> abilities;
}