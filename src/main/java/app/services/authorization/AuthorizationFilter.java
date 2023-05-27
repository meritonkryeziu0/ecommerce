package app.services.authorization;

import app.context.UserContext;
import app.services.authorization.ability.Ability;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.RolesService;
import io.quarkus.arc.Arc;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;


import static app.utils.Utils.notBlank;
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
      String userRole = userContext.getRole();
      Ability actionAbility = new Ability(action);
      if(userRoleMatchesAction(userRole, actionAbility)){
        return Uni.createFrom().voidItem();
      }
    }
    return Uni.createFrom().failure(new AuthorizationException(AuthorizationException.FORBIDDEN, Response.Status.FORBIDDEN));
  }

  private boolean userRoleMatchesAction(String role, Ability actionAbility){
    if(notNull(role) && notBlank(rolesService.getAbilities(role))){
      return rolesService.getAbilities(role)
          .stream().anyMatch(roleAbility -> roleAbility.getId().equals(actionAbility.constructId()));
    }
    return false;
  }

}
