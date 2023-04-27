package app.services.roles;

import app.services.authorization.ability.Ability;
import app.services.authorization.ability.RoleWithAbilities;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.PermitAll;
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
  public Uni<List<RoleWithAbilities>> list() {
    return RoleWithAbilities.listAll();
  }

  //todo:add post to add roles:
  @POST
  public Uni<RoleWithAbilities> addAbilityToRole(@Valid RoleWithAbilityWrapper wrapper){
    return rolesService.addAbilityToRole(wrapper.getRole(), wrapper.getAbilities());
  }
}
