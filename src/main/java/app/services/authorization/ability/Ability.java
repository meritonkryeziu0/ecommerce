package app.services.authorization.ability;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ability {
  private String id;
  @NotBlank
  private String module;
  @NotBlank
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

  public static Ability fromShortFormat(String id){
    String[] properties = id.split(":");
    Ability ability = new Ability();
    ability.setModule(properties[0]);
    ability.setAction(properties[1]);
    ability.setId(id);
    return ability;
  }

  public Ability fromLongFormat(){
    this.id = this.constructId();
    return this;
  }
}
