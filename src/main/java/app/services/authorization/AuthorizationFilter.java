package app.services.authorization;

import app.context.UserContext;
import app.services.authorization.ability.Ability;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.RolesService;
import io.quarkus.arc.Arc;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;

import static app.utils.Utils.notNull;

public class AuthorizationFilter {
  @Inject
  RolesService rolesService;
  @ServerRequestFilter
  public Uni<Void> filter(ContainerRequestContext requestContext, ResourceInfo resourceInfo){
    ActionAbility action = resourceInfo.getResourceMethod().getAnnotation(ActionAbility.class);
    PermitAll permitAll = resourceInfo.getResourceMethod().getAnnotation(PermitAll.class);
    if(notNull(permitAll)){
      return Uni.createFrom().voidItem();
    }
    if(notNull(action) && requestContext.getSecurityContext().getUserPrincipal() instanceof DefaultJWTCallerPrincipal){
      UserContext userContext = Arc.container().instance(UserContext.class).get();
      Ability actionAbility = new Ability(action);
      if(abilityNotAllowed(userContext, actionAbility)){
        return Uni.createFrom()
            .failure(new AuthorizationException(
                AuthorizationException.FORBIDDEN, Response.Status.FORBIDDEN));
      }
    }
    return Uni.createFrom().voidItem();
  }

  private boolean abilityNotAllowed(UserContext userContext, Ability actionAbility){
    if(notNull(rolesService.getAbilities(userContext.getRole()))){
      return rolesService.getAbilities(userContext.getRole())
          .stream().noneMatch(roleAbility -> roleAbility.getId().equals(actionAbility.constructId()));
    }
    return false;
  }

}