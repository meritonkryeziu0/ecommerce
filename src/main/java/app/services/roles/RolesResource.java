package app.services.roles;

import app.services.authorization.ability.ActionAbility;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.services.roles.models.RoleWithAbilities;
import app.services.roles.models.RoleWithAbilityWrapper;
import app.shared.SuccessResponse;
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
  public Uni<RoleWithAbilities> add(@Valid RoleWithAbilityWrapper wrapper){
    return rolesService.addRoleWithAbility(wrapper.getRole(), wrapper.getAbilities());
  }

  @PUT
  @ActionAbility(action = Actions.CREATE, module = Modules.Roles)
  public Uni<RoleWithAbilities> addAbilityToRole(@Valid RoleWithAbilityWrapper wrapper){
    return rolesService.addAbilityToRole(wrapper.getRole(), wrapper.getAbilities());
  }
  @PUT
  @Path("/{role}/{abilityId}")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Roles)
  public Uni<RoleWithAbilities> removeAbilityFromRole(@PathParam("role") String role, @PathParam("abilityId") String abilityId){
    return rolesService.removeAbilityFromRole(role, abilityId);
  }

  @DELETE
  @Path("/{role}")
  @ActionAbility(action = Actions.DELETE, module = Modules.Roles)
  public Uni<SuccessResponse> delete(@PathParam("role") String role){
    return rolesService.delete(role);
  }
}