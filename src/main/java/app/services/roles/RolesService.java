package app.services.roles;

import app.common.CustomValidator;
import app.mongodb.MongoUtils;
import app.services.authorization.ability.Ability;
import app.services.roles.models.RoleWithAbilities;
import app.utils.Utils;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Startup
public class RolesService {

  @Inject
  Logger logger;
  @Inject
  RolesRepository repository;

  @Inject
  CustomValidator validator;

  public Map<String, List<Ability>> roleWithAbilities = new HashMap<>();

  public Map<String, List<Ability>> getRolesWithAbilities(){
    return this.roleWithAbilities;
  }

  public List<Ability> getAbilities(String role){
    if(this.roleWithAbilities.containsKey(role)){
      return this.roleWithAbilities.get(role);
    }
    return null;
  }
  void onStart(@Observes StartupEvent ev) {
    logger.info("Intializing roles...");
    RoleWithAbilities.listAll()
            .subscribe()
            .with(reactivePanacheMongoEntityBase -> {
              this.roleWithAbilities = reactivePanacheMongoEntityBase.stream()
                      .map(Utils.mapTo(RoleWithAbilities.class))
                      .collect(Collectors.toMap(RoleWithAbilities::getRole, RoleWithAbilities::getAbilities));
            });
  }

  public Uni<RoleWithAbilities> addRoleWithAbility(@NotNull String role, @Valid List<Ability> abilities){
    return validator.validate(abilities)
            .flatMap(validAbilities -> {
              List<Ability> collect = abilities.stream().map(Ability::fromLongFormat).collect(Collectors.toList());
              RoleWithAbilities roleWithAbilities = RoleWithAbilities.construct(role, collect);
              return MongoUtils.addEntity(roleWithAbilities);
            });
  }

  public Uni<RoleWithAbilities> addAbilityToRole(@NotNull String role, @Valid List<Ability> abilities){
    List<Ability> collect = abilities.stream().map(Ability::fromLongFormat).collect(Collectors.toList());
    return repository.addAbilityToRole(role, collect);
  }
}