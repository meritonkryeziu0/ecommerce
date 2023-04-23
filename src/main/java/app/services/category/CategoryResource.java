package app.services.category;

import app.helpers.PaginatedResponse;
import app.services.accounts.models.Roles;
import app.services.category.models.Category;
import app.services.category.models.CreateCategory;
import app.services.category.models.UpdateCategory;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
  @Inject
  CategoryService service;
  @GET
  @RolesAllowed(Roles.Fields.Admin)
  public Uni<PaginatedResponse<Category>> getList(@BeanParam CategoryFilterWrapper wrapper){
    return service.getList(wrapper);
  }

  @GET
  @Path("/{id}")
  @RolesAllowed(Roles.Fields.Everyone)
  public Uni<Category> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @POST
  @RolesAllowed({Roles.Fields.Admin})
  public Uni<Category> add(CreateCategory createCategory) {
    return service.add(createCategory);
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({Roles.Fields.Admin})
  public Uni<Category> update(@PathParam("id") String id, UpdateCategory updateCategory) {
    return service.update(id, updateCategory);

  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({Roles.Fields.Admin})
  public Uni<Void> delete(@PathParam("id") String id) {
    return service.delete(id);
  }
}
