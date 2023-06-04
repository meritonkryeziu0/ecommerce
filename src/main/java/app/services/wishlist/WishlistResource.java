package app.services.wishlist;

import app.context.UserContext;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.services.product.models.ProductReference;
import app.services.wishlist.models.Wishlist;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/wishlist")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WishlistResource {
  @Inject
  WishlistService service;

  @Inject
  UserContext userContext;

  @GET
  @ActionAbility(action = Actions.READ, module = Modules.Wishlist)
  public Uni<Wishlist> getWishlistByUserId() {
    return service.getByUserId(userContext.getId());
  }

  @PUT
  @ActionAbility(action = Actions.UPDATE, module = Modules.Wishlist)
  public Uni<Wishlist> update(ProductReference productReference) {
    return service.update(userContext.getId(), productReference);
  }

  @PUT
  @Path("/update-quantity")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Wishlist)
  public Uni<Wishlist> updateProductQuantity(ProductReference productReference){
    return service.updateProductQuantity(userContext.getId(), productReference);
  }

  @PUT
  @Path("/to-cart")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Wishlist)
  public Uni<Wishlist> addProductToCart(ProductReference productReference) {
    return service.addProductToCart(userContext.getId(), productReference);
  }

  @DELETE
  @Path("/remove")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Wishlist)
  public Uni<Wishlist> removeProductFromWishlist(ProductReference productReference) {
    return service.removeProductFromWishlist(userContext.getId(), productReference);
  }

  @DELETE
  @ActionAbility(action = Actions.UPDATE, module = Modules.Wishlist)
  public Uni<SuccessResponse> emptyWishlist() {
    return service.emptyWishlist(userContext.getId());
  }
}
