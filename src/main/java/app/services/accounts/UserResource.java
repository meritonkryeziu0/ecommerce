package app.services.accounts;

import app.context.UserContext;
import app.helpers.PaginatedResponse;
import app.services.accounts.models.*;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.PermitAll;
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
  @ActionAbility(action = Actions.LIST, module = Modules.User)
  public Uni<PaginatedResponse<User>> getList(@BeanParam UserFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @GET
  @Path("/my-profile/{id}")
//  @ActionAbility(action = Actions.READ, module = Modules.User)
  @PermitAll
  public Uni<User> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @GET
  @Path("/my-profile")
  @ActionAbility(action = Actions.SELF_READ, module = Modules.User)
  public Uni<User> getProfile() {
    return service.getById(userContext.getId());
  }

  @POST
  @ActionAbility(action = Actions.CREATE, module = Modules.User)
  public Uni<User> add(CreateUser createUser) {
    return service.add(createUser);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(action = Actions.UPDATE, module = Modules.User)
  public Uni<User> update(@PathParam("id") String id, UpdateUser updateUser) {
    return service.update(id, updateUser);
  }

  @PUT
  @Path("/{id}/role")
  @ActionAbility(action = Actions.UPDATE, module = Modules.User)
  public Uni<User> updateRole(@PathParam("id") String id, UpdateRole updateRole) {
    return service.updateRole(id, updateRole);
  }


  //Users can update themselves
  @PUT
  @ActionAbility(action = Actions.SELF_UPDATE, module = Modules.User)
  public Uni<User> update(UpdateUser updateUser) {
    return service.update(userContext.getId(), updateUser);
  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(action = Actions.DELETE, module = Modules.User)
  public Uni<SuccessResponse> delete(@PathParam("id") String id) {
    return service.delete(id);
  }

  @PUT
  @Path("/{userId}/shipping-address/")
  @ActionAbility(action = Actions.UPDATE, module = Modules.User)
  public Uni<User> addShippingAddress(@PathParam("userId") String userId, ShippingAddress shippingAddress) {
    return service.addShippingAddress(userId, shippingAddress);
  }

  @PUT
  @Path("/my-profile/shipping-address")
  @ActionAbility(action = Actions.SELF_UPDATE, module = Modules.User)
  public Uni<User> addShippingAddressSelf(ShippingAddress shippingAddress) {
    return service.addShippingAddress(userContext.getId(), shippingAddress);
  }

  @PUT
  @Path("/{userId}/shipping-address/{shippingId}")
  @ActionAbility(action = Actions.UPDATE, module = Modules.User)
  public Uni<User> editShippingAddress(@PathParam("userId") String userId, @PathParam("shippingId") String shippingId, ShippingAddress shippingAddress) {
    return service.editShippingAddress(userId, shippingId, shippingAddress);
  }

  @PUT
  @Path("/my-profile/shipping-address/{shippingId}")
  @ActionAbility(action = Actions.SELF_UPDATE, module = Modules.User)
  public Uni<User> editShippingAddressSelf(@PathParam("shippingId") String shippingId, ShippingAddress shippingAddress) {
    return service.editShippingAddress(userContext.getId(), shippingId, shippingAddress);
  }

  @DELETE
  @Path("/{userId}/shipping-address/")
  @ActionAbility(action = Actions.UPDATE, module = Modules.User)
  public Uni<User> deleteShippingAddress(@PathParam("userId") String userId, ShippingAddress shippingAddress) {
    return service.deleteShippingAddress(userId, shippingAddress);
  }

  @DELETE
  @Path("/my-profile/shipping-address/")
  @ActionAbility(action = Actions.SELF_UPDATE, module = Modules.User)
  public Uni<User> deleteShippingAddressSelf(ShippingAddress shippingAddress) {
    return service.deleteShippingAddress(userContext.getId(), shippingAddress);
  }
}