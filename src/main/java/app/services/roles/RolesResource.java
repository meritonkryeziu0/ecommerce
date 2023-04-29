package app.services.roles;

import app.services.authorization.ability.ActionAbility;
import app.services.authorization.ability.RoleWithAbilities;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RolesResource {

  @Inject
  RolesService rolesService;
  @GET
  @Authenticated
  public Uni<List<RoleWithAbilities>> list() {
    return RoleWithAbilities.listAll();
  }

  @POST
  @ActionAbility(action = Actions.CREATE, module = Modules.Roles)
  public Uni<RoleWithAbilities> addAbilityToRole(@Valid RoleWithAbilityWrapper wrapper){
    return rolesService.addAbilityToRole(wrapper.getRole(), wrapper.getAbilities());
  }
}
