package app.services.roles.models;

import app.mongodb.MongoCollections;
import app.services.authorization.ability.Ability;
import app.shared.BaseModel;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@MongoEntity(collection = MongoCollections.ROLES_COLLECTION)
public class RoleWithAbilities extends BaseModel {
  public static final String FIELD_ROLE = "role";
  public static final String FIELD_ABILITIES = "abilities";
  private String role;
  private List<Ability> abilities;

  public static RoleWithAbilities construct(String role, List<Ability> abilities){
    RoleWithAbilities roleWithAbilities = new RoleWithAbilities();
    roleWithAbilities.setRole(role);
    roleWithAbilities.setAbilities(abilities);
    return roleWithAbilities;
  }
}