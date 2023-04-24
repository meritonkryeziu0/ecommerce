package app.services.authorization;

import app.services.context.UserContext;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.graalvm.collections.Pair;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static app.utils.Utils.notBlank;

@ApplicationScoped
public class AuthorizationService {

  @Inject
  RolesService rolesService;

  @Inject
  Logger logger;

  public Uni<HashMap<String, IsAuthorizedResult>> isAuthorized(UserContext userContext, List<String> actionAbilities){
    List<Multi<Pair<String, IsAuthorizedResult>>> concatUnis = actionAbilities.stream()
        .map(actionAbility -> isAuthorized(userContext, actionAbility)
            .map(result -> Pair.create(actionAbility, result)))
        .map(Uni::toMulti).collect(Collectors.toList());

    return Multi.createBy().concatenating()
        .streams(concatUnis)
        .collect()
        .asList()
        .map(pairs -> {
          HashMap<String, IsAuthorizedResult> permissionsMap = new HashMap<>(pairs.size());
          pairs.forEach(pair -> permissionsMap.put(pair.getLeft(), pair.getRight()));
          return permissionsMap;
    });
  }

  public Uni<IsAuthorizedResult> isAuthorized(UserContext userContext, String actionAbility){
    Ability ability = Ability.fromShortId(actionAbility);
    return isAuthorized(userContext, ability);
  }

  public Uni<IsAuthorizedResult> isAuthorized(UserContext userContext, Ability ability){
    IsAuthorizedResult authorizedResult = new IsAuthorizedResult(false);
    if(notBlank(userContext.getRole())){
      Optional<Ability> matchedAbility =  rolesService.getRolesWithAbilities()
          .get(userContext.getRole()).stream()
          .filter(roleAbility -> roleAbility.getId().equals(ability.constructId()))
          .findFirst();
      if(matchedAbility.isEmpty()){
        return Uni.createFrom().item(new IsAuthorizedResult());
      }
      authorizedResult.setAuthorized(true);
      return Uni.createFrom().item(authorizedResult);
    }
    logger.warn("UserContext role is null");
    return Uni.createFrom().failure(new AuthorizationException(AuthorizationException.USERCONTEX_ROLE_NOT_SET, Response.Status.BAD_REQUEST));
  }
}
