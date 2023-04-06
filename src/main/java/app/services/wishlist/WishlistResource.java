package app.services.wishlist;

import app.services.accounts.models.Roles;
import app.services.product.models.ProductReference;
import app.services.wishlist.models.Wishlist;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/user/{userId}/wishlist")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@RolesAllowed({Roles.Fields.Everyone})
public class WishlistResource {

  @Inject
  WishlistService service;

  @GET
  public Uni<Wishlist> getWishlistByUserId(@PathParam("userId") String userId) {
    return service.getByUserId(userId);
  }

  @PUT
  public Uni<Wishlist> update(@PathParam("userId") String id, ProductReference productReference) {
    return service.update(id, productReference);
  }

  @PUT
  @Path("/to-cart")
  public Uni<Wishlist> addProductToCart(@PathParam("userId") String userId, ProductReference productReference) {
    return service.addProductToCart(userId, productReference);
  }

  @DELETE
  @Path("/remove")
  public Uni<Wishlist> removeProductFromWishlist(@PathParam("userId") String userId, ProductReference productReference) {
    return service.removeProductFromWishlist(userId, productReference);
  }

  @DELETE
  public Uni<SuccessResponse> emptyWishlist(@PathParam("userId") String userId) {
    return service.emptyWishlist(userId);
  }
}
