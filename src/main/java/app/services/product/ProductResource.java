package app.services.product;

import app.context.UserContext;
import app.helpers.PaginatedResponse;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.services.product.models.CreateProduct;
import app.services.product.models.Product;
import app.services.product.models.UpdateProduct;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {
  @Inject
  ProductService service;

  @Inject
  UserContext userContext;

  @GET
  @PermitAll
  public Uni<PaginatedResponse<Product>> getList(@BeanParam ProductFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @GET
  @Path("/{id}")
  @PermitAll
  public Uni<Product> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @POST
  @ActionAbility(action = Actions.CREATE, module = Modules.Product)
  public Uni<Product> add(CreateProduct createProduct) {
    return service.add(createProduct);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Product)
  public Uni<Product> update(@PathParam("id") String id, UpdateProduct updateProduct) {
    return service.update(id, updateProduct);

  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(action = Actions.DELETE, module = Modules.Product)
  public Uni<Void> delete(@PathParam("id") String id) {
    return service.delete(id);
  }
}