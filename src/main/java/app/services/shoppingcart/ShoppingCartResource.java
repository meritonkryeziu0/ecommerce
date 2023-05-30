package app.services.shoppingcart;

import app.context.UserContext;
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

@Path("/shopping-cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShoppingCartResource {
  @Inject
  ShoppingCartService service;

  @Inject
  UserContext userContext;
  @GET
  @ActionAbility(action = Actions.READ, module = Modules.Shoppingcart)
  public Uni<ShoppingCart> getByUserId() {
    return service.getByUserId(userContext.getId());
  }

  @PUT
  @ActionAbility(action = Actions.UPDATE, module = Modules.Shoppingcart)
  public Uni<ShoppingCart> update(ProductReference productReference) {
    return service.update(null, userContext.getId(), productReference);
  }

  @POST
  @Path("/buy")
  @ActionAbility(action = Actions.CREATE, module = Modules.Order)
  public Uni<ShoppingCart> buy(OrderDetails orderDetails) {
    return service.buyShoppingCart(userContext.getId(), orderDetails);
  }

  @PUT
  @Path("/remove-product")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Shoppingcart)
  public Uni<ShoppingCart> removeProduct(ProductReference productReference) {
    return service.removeProductFromCart(userContext.getId(), productReference);
  }

  @DELETE
  @ActionAbility(action = Actions.UPDATE, module = Modules.Shoppingcart)
  public Uni<SuccessResponse> emptyShoppingCart() {
    return service.emptyShoppingCart(userContext.getId());
  }
}