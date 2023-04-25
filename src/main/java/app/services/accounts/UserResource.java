package app.services.accounts;

import app.context.UserContext;
import app.helpers.PaginatedResponse;
import app.services.accounts.models.*;
import app.services.authorization.ability.ActionAbility;
import app.services.authorization.roles.Operation;
import app.services.authorization.roles.Modules;
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
  @ActionAbility(
      role = {
          Roles.ADMIN
      },
      action = Operation.LIST, module = Modules.User)
  public Uni<PaginatedResponse<User>> getList(@BeanParam UserFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @GET
  @Path("/{id}")
  @ActionAbility(
      role = {
          Roles.ADMIN
      },
      action = Operation.READ, module = Modules.User)
  public Uni<User> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @POST
  @ActionAbility(
      role = {
          Roles.ADMIN
      },
      action = Operation.CREATE, module = Modules.User)
  public Uni<User> add(CreateUser createUser) {
    return service.add(createUser);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(
      role = {
          Roles.ADMIN
      },
      action = Operation.UPDATE, module = Modules.User)
  public Uni<User> update(@PathParam("id") String id, UpdateUser updateUser) {
    return service.update(id, updateUser);
  }

  //Users can update themselves
  @PUT
  @ActionAbility(
      role = {
          Roles.USER,
          Roles.ADMIN
      },
      action = Operation.SELF_UPDATE, module = Modules.User)
  public Uni<User> update(UpdateUser updateUser) {
    return service.update(userContext.getUserId(), updateUser);
  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(
      role = {
          Roles.ADMIN
      },
      action = Operation.DELETE, module = Modules.User)
  public Uni<SuccessResponse> delete(@PathParam("id") String id) {
    return service.delete(id);
  }
}