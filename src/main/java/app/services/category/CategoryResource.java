package app.services.category;

import app.helpers.PaginatedResponse;
import app.services.accounts.models.Roles;
import app.services.authorization.ability.ActionAbility;
import app.services.authorization.roles.Operation;
import app.services.authorization.roles.Modules;
import app.services.category.models.Category;
import app.services.category.models.CreateCategory;
import app.services.category.models.UpdateCategory;
import io.smallrye.mutiny.Uni;

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
  @ActionAbility(
          role = {
                  Roles.USER,
                  Roles.ADMIN
          },
          action = Operation.LIST, module = Modules.Category)
  public Uni<PaginatedResponse<Category>> getList(@BeanParam CategoryFilterWrapper wrapper){
    return service.getList(wrapper);
  }

  @GET
  @Path("/{id}")
  @ActionAbility(
          role = {
                  Roles.USER,
                  Roles.ADMIN
          },
          action = Operation.READ, module = Modules.Category)
  public Uni<Category> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @POST
  @ActionAbility(
          role = {
                  Roles.ADMIN
          },
          action = Operation.CREATE, module = Modules.Category)
  public Uni<Category> add(CreateCategory createCategory) {
    return service.add(createCategory);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(
          role = {
                  Roles.ADMIN
          },
          action = Operation.UPDATE, module = Modules.Category)
  public Uni<Category> update(@PathParam("id") String id, UpdateCategory updateCategory) {
    return service.update(id, updateCategory);

  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(
          role = {
                  Roles.ADMIN
          },
          action = Operation.DELETE, module = Modules.Category)
  public Uni<Void> delete(@PathParam("id") String id) {
    return service.delete(id);
  }
}
