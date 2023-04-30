package app.services.seller;

import app.context.UserContext;
import app.helpers.PaginatedResponse;
import app.services.accounts.models.CreateUser;
import app.services.accounts.models.UpdateUser;
import app.services.accounts.models.User;
import app.services.accounts.models.UserFilterWrapper;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/seller")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SellerResource {
  @Inject
  SellerService service;
  @Inject
  UserContext userContext;

  @GET
  @ActionAbility(action = Actions.LIST, module = Modules.Seller)
  public Uni<PaginatedResponse<User>> getList(@BeanParam UserFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @POST
  @ActionAbility(action = Actions.CREATE, module = Modules.User)
  public Uni<User> add(CreateUser createUser) {
    return service.addSeller(createUser);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(action = Actions.UPDATE, module = Modules.User)
  public Uni<User> update(@PathParam("id") String id, UpdateUser updateUser) {
    return service.update(id, updateUser);
  }

  //Sellers can update themselves
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
}