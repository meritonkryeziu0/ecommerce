package app.services.shoppingcart;

import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/users/{userId}/shopping-cart")
//@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShoppingCartResource {
  @Inject
  ShoppingCartService service;

  @GET
  public Uni<Void> test(){
    return Uni.createFrom().voidItem();
  }

}
