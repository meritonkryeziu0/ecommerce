package app.services.shoppingcart;

import app.services.authorization.ability.ActionAbility;
import app.services.order.models.OrderDetails;
import app.services.product.models.ProductReference;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.services.shoppingcart.models.ShoppingCart;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/users/{userId}/shopping-cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShoppingCartResource {
  @Inject
  ShoppingCartService service;

  @GET
  @ActionAbility(action = Actions.READ, module = Modules.Shoppingcart)
  public Uni<ShoppingCart> getByUserId(@PathParam("userId") String userId) {
    return service.getByUserId(userId);
  }

  @PUT
  @ActionAbility(action = Actions.UPDATE, module = Modules.Shoppingcart)
  public Uni<ShoppingCart> update(@PathParam("userId") String id, ProductReference productReference) {
    return service.update(null, id, productReference);
  }

  @POST
  @Path("/buy")
  @ActionAbility(action = Actions.CREATE, module = Modules.Order)
  public Uni<ShoppingCart> buy(@PathParam("userId") String id, OrderDetails orderDetails) {
    return service.buyShoppingCart(id, orderDetails);
  }

  @DELETE
  public Uni<SuccessResponse> emptyShoppingCart(@PathParam("userId") String userId) {
    return service.emptyShoppingCart(userId);
  }
}
