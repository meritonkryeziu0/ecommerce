package app.services.category;

import app.helpers.PaginatedResponse;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.services.category.models.Category;
import app.services.category.models.CreateCategory;
import app.services.category.models.UpdateCategory;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
  @Inject
  CategoryService service;
  @GET
  @ActionAbility(action = Actions.LIST, module = Modules.Category)
  public Uni<PaginatedResponse<Category>> getList(@BeanParam CategoryFilterWrapper wrapper){
    return service.getList(wrapper);
  }

  @GET
  @Path("mainCategories")
  @PermitAll
  public Uni<List<Category>> getMainCategories() {
    return service.getMainCategories();
  }

  @GET
  @Path("{mainCategoryName}/subcategories")
  @PermitAll
  public Uni<List<Category>> getSubcategories(@PathParam("mainCategoryName") String mainCategoryName) {
    return service.getListByParentCategoryName(mainCategoryName);
  }

  @GET
  @Path("/{id}")
  @ActionAbility(action = Actions.READ, module = Modules.Category)
  public Uni<Category> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @POST
  @ActionAbility(action = Actions.CREATE, module = Modules.Category)
  public Uni<Category> add(CreateCategory createCategory) {
    return service.add(createCategory);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Category)
  public Uni<Category> update(@PathParam("id") String id, UpdateCategory updateCategory) {
    return service.update(id, updateCategory);
  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(action = Actions.DELETE, module = Modules.Category)
  public Uni<SuccessResponse> delete(@PathParam("id") String id) {
    return service.delete(id).map(unused -> SuccessResponse.toSuccessResponse());
  }
}
