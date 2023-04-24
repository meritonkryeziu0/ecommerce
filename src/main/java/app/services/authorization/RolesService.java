package app.services.authorization;

import app.utils.Utils;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Startup
public class RolesService {

  @Inject
  Logger logger;

  public Map<String, List<Ability>> roleWithAbilities = new HashMap<>();
  void onStart(@Observes StartupEvent ev) {
    logger.info("Intializing roles");
    RoleWithAbilities.listAll().subscribe()
        .with(reactivePanacheMongoEntityBase -> {
          this.roleWithAbilities = reactivePanacheMongoEntityBase.stream()
              .map(Utils.mapTo(RoleWithAbilities.class))
              .collect(Collectors.toMap(RoleWithAbilities::getRole, RoleWithAbilities::getAbilities));
        });
  }

  public Map<String, List<Ability>> getRolesWithAbilities(){
    return this.roleWithAbilities;
  }
}
