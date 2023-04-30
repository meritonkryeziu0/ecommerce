package app.services.promotion;

import app.context.UserContext;
import app.services.authorization.ability.ActionAbility;
import app.services.product.models.Product;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

@Path("/seller")
public class PromotionResource {
  @Inject
  UserContext userContext;
  @Inject
  PromotionService service;

  @GET
  @Path("/promoted-products")
  @ActionAbility(action = Actions.LIST, module = Modules.Promotion)
  public Uni<List<Product>> getList() {
    return service.getPromotedProducts(userContext.getId());
  }

  @POST
  @Path("/products/{product_id}/promote")
  @ActionAbility(action = Actions.CREATE, module = Modules.Promotion)
  public Uni<SuccessResponse> promote(@PathParam("product_id") String productId) {
    return service.promoteProduct(userContext.getId(), productId);
  }

  @DELETE
  @Path("/products/{productId}/promote")
  @ActionAbility(action = Actions.DELETE, module = Modules.Promotion)
  public Uni<SuccessResponse> unPromote(@PathParam("productId") String productId) {
    return service.unPromoteProduct(userContext.getId(), productId);
  }

}
