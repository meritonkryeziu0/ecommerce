package app.services.authorization;

import app.exceptions.BaseException;
import app.services.context.UserContext;
import io.quarkus.arc.Arc;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jose4j.jwk.Use;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

import static app.utils.Utils.isNull;
import static app.utils.Utils.notNull;

public class AuthorizationFilter {

  private ContainerRequestContext requestContext;
  private ResourceInfo resourceInfo;

  @Inject
  RolesService rolesService;
  @ServerRequestFilter
  public Uni<Void> filter(ContainerRequestContext requestContext, ResourceInfo resourceInfo){
    ActionAbility action = resourceInfo.getResourceMethod().getAnnotation(ActionAbility.class);
    if(isNull(action) && isNull(resourceInfo.getResourceMethod().getAnnotation(PermitAll.class))){
      return Uni.createFrom().failure(new BaseException("Unauthorized", Response.Status.UNAUTHORIZED));
    }
    if(requestContext.getSecurityContext().getUserPrincipal() instanceof DefaultJWTCallerPrincipal){
      UserContext userContext = Arc.container().instance(UserContext.class).get();
      Ability actionAbility = new Ability(action);
      Optional<Ability> allowedAbility =  rolesService.getRoles().get(userContext.getRole())
          .stream().filter(roleAbility -> roleAbility.getId().equals(actionAbility.constructId()))
          .findAny();
      if(allowedAbility.isEmpty()){
        return Uni.createFrom().failure(new BaseException("Unauthorized", Response.Status.UNAUTHORIZED));
      }
    }
    return Uni.createFrom().voidItem();
  }

}
