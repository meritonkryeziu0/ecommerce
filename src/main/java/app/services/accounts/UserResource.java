package app.services.accounts;

import app.context.UserContext;
import app.helpers.PaginatedResponse;
import app.services.accounts.models.*;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.Operation;
import app.services.roles.Modules;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
  @Inject
  UserService service;

  @Inject
  UserContext userContext;

  @GET
  @ActionAbility(action = Operation.LIST, module = Modules.User)
  public Uni<PaginatedResponse<User>> getList(@BeanParam UserFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @GET
  @Path("/{id}")
  @ActionAbility(action = Operation.READ, module = Modules.User)
  public Uni<User> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @POST
  @ActionAbility(action = Operation.CREATE, module = Modules.User)
  public Uni<User> add(CreateUser createUser) {
    return service.add(createUser);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(action = Operation.UPDATE, module = Modules.User)
  public Uni<User> update(@PathParam("id") String id, UpdateUser updateUser) {
    return service.update(id, updateUser);
  }

  //Users can update themselves
  @PUT
  @ActionAbility(action = Operation.SELF_UPDATE, module = Modules.User)
  public Uni<User> update(UpdateUser updateUser) {
    return service.update(userContext.getUserId(), updateUser);
  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(action = Operation.DELETE, module = Modules.User)
  public Uni<SuccessResponse> delete(@PathParam("id") String id) {
    return service.delete(id);
  }
}