package app.services.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ability {
  private String id;
  private String module;
  private String action;

  public String constructId(ActionAbility actionAbility){
    return String.join(":", actionAbility.module(), actionAbility.action());
  }

  public Ability(ActionAbility actionAbility){
    this.id = constructId(actionAbility);
    this.module = actionAbility.module();
    this.action = actionAbility.action();
  }

}
