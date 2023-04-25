package app.services.authorization.ability;

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

  public String constructId(){
    return String.join(":", this.module, this.action);
  }

  public Ability(ActionAbility actionAbility){
    this.id = constructId(actionAbility);
    this.module = actionAbility.module();
    this.action = actionAbility.action();
  }

  public static Ability fromShortId(String id){
    String[] properties = id.split(":");
    Ability ability = new Ability();
    ability.setModule(properties[0]);
    ability.setAction(properties[1]);
    ability.setId(id);
    return ability;
  }
}
