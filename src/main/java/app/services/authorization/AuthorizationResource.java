package app.services.authorization;

import app.context.UserContext;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;

@Path("/authorization/permissions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authorization")
@RequestScoped
public class AuthorizationResource {
  @Inject
  UserContext userContext;
  @Inject
  AuthorizationService service;

  @POST
  @PermitAll
  public Uni<HashMap<String, AuthorizedResult>> isAuthorized(List<String> actionAbilityIds){
    return service.isAuthorized(userContext, actionAbilityIds);
  }
}
