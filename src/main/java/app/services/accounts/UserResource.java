package app.services.accounts;

import app.helpers.PaginatedResponse;
import app.services.accounts.models.CreateUser;
import app.services.accounts.models.UpdateUser;
import app.services.accounts.models.User;
import app.services.accounts.models.UserFilterWrapper;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
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
  JsonWebToken token;

  @GET
  @RolesAllowed({"Admin"})
  public Uni<PaginatedResponse<User>> getList(@BeanParam UserFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({"Everyone"})
  public Uni<User> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @POST
  @RolesAllowed({"Admin"})
  public Uni<User> add(CreateUser createUser) {
    return service.add(createUser);
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({"Admin"})
  public Uni<User> update(@PathParam("id") String id, UpdateUser updateUser) {
    return service.update(id, updateUser);
  }

  //Users can update themselves
  @PUT
  @RolesAllowed({"Everyone"})
  public Uni<User> update(UpdateUser updateUser) {
    return service.update(token.getClaim("userId"), updateUser);
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({"Admin"})
  public Uni<SuccessResponse> delete(@PathParam("id") String id) {
    return service.delete(id);
  }
}