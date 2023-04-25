package app.services.wishlist;

import app.services.accounts.models.Roles;
import app.services.authorization.ability.ActionAbility;
import app.services.authorization.roles.Operation;
import app.services.authorization.roles.Modules;
import app.services.product.models.ProductReference;
import app.services.wishlist.models.Wishlist;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/user/{userId}/wishlist")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WishlistResource {

  @Inject
  WishlistService service;

  @GET
  @ActionAbility(
      role = {
          Roles.USER,
          Roles.ADMIN
      },
      action = Operation.READ, module = Modules.Wishlist)
  public Uni<Wishlist> getWishlistByUserId(@PathParam("userId") String userId) {
    return service.getByUserId(userId);
  }

  @PUT
  @ActionAbility(
      role = {
          Roles.USER,
          Roles.ADMIN
      },
      action = Operation.UPDATE, module = Modules.Wishlist)
  public Uni<Wishlist> update(@PathParam("userId") String userId, ProductReference productReference) {
    return service.update(userId, productReference);
  }

  @PUT
  @Path("/update-quantity")
  @ActionAbility(
      role = {
          Roles.USER,
          Roles.ADMIN
      },
      action = Operation.UPDATE, module = Modules.Wishlist)
  public Uni<Wishlist> updateProductQuantity(@PathParam("userId") String userId, ProductReference productReference){
    return service.updateProductQuantity(userId, productReference);
  }

  @PUT
  @Path("/to-cart")
  @ActionAbility(
      role = {
          Roles.USER,
          Roles.ADMIN
      },
      action = Operation.UPDATE, module = Modules.Wishlist)
  public Uni<Wishlist> addProductToCart(@PathParam("userId") String userId, ProductReference productReference) {
    return service.addProductToCart(userId, productReference);
  }

  @DELETE
  @Path("/remove")
  @ActionAbility(
      role = {
          Roles.USER,
          Roles.ADMIN
      },
      action = Operation.UPDATE, module = Modules.Wishlist)
  public Uni<Wishlist> removeProductFromWishlist(@PathParam("userId") String userId, ProductReference productReference) {
    return service.removeProductFromWishlist(userId, productReference);
  }

  @DELETE
  @ActionAbility(
      role = {
          Roles.USER,
          Roles.ADMIN
      },
      action = Operation.UPDATE, module = Modules.Wishlist)
  public Uni<SuccessResponse> emptyWishlist(@PathParam("userId") String userId) {
    return service.emptyWishlist(userId);
  }
}
