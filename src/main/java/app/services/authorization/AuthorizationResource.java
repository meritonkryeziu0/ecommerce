package app.services.authorization;

import app.services.context.UserContext;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("authorization")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorizationResource {
  @Inject
  UserContext userContext;
  @Inject RolesService rolesService;

  @GET
  public Uni<AuthorizationResult> isAuthorized(String actionAbilityId){
    Ability ability = Ability.fromShortId(actionAbilityId);
    Optional<Ability> rolesAbility = rolesService.getRoles().get(userContext.getRole())
        .stream().filter(roleAbility -> roleAbility.getId().equals(ability.constructId())).findAny();
    AuthorizationResult.AuthorizationResultBuilder authorizationResult = AuthorizationResult.builder()
        .isAuthorized(false)
        .module(ability.getModule())
        .action(ability.getAction());
    if(rolesAbility.isPresent()){
      authorizationResult.isAuthorized(true);
    }
    return authorizationResult.build().asUni();
  }
}
