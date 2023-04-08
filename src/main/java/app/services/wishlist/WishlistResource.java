package app.services.wishlist;

import app.services.shoppingCart.models.ShoppingCart;
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
  public Uni<Wishlist> getWishlistByUserId(@PathParam("userId") String userId) {
    return service.getByUserId(userId);
  }

  @DELETE
  public Uni<SuccessResponse> emptyWishlist(@PathParam("userId") String userId) {
    return service.emptyWishlist(userId);
  }
}
