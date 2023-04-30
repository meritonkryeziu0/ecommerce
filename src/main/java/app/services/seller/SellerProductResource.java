package app.services.seller;

import app.context.UserContext;
import app.services.authorization.ability.ActionAbility;
import app.services.product.ProductService;
import app.services.product.models.CreateProduct;
import app.services.product.models.Product;
import app.services.product.models.UpdateProduct;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/seller")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SellerProductResource {
  @Inject
  ProductService productService;

  @Inject
  SellerService sellerService;

  @Inject
  UserContext userContext;

  @GET
  @Path("/products")
  @ActionAbility(action = Actions.LIST, module = Modules.Product)
  public Uni<List<Product>> getList() {
    return sellerService.getSellerProducts(userContext.getId());
  }

  @GET
  @Path("{sellerId}/products")
  @ActionAbility(action = Actions.LIST, module = Modules.Product)
  public Uni<List<Product>> getList(@PathParam("sellerId") String sellerId) {
    return sellerService.getSellerProducts(sellerId);
  }

  @POST
  @Path("/products")
  @ActionAbility(action = Actions.CREATE, module = Modules.Product)
  public Uni<SuccessResponse> addProduct(CreateProduct createProduct) {
    return sellerService.addProduct(userContext, createProduct);
  }

  @PUT
  @Path("/products/{productId}")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Product)
  public Uni<SuccessResponse> update(@PathParam("productId") String productId, UpdateProduct updateProduct) {
    return sellerService.updateProduct(userContext, productId, updateProduct);
  }

  @DELETE
  @Path("/products/{productId}")
  @ActionAbility(action = Actions.DELETE, module = Modules.Product)
  public Uni<SuccessResponse> deleteProduct(@PathParam("productId") String productId) {
    return sellerService.deleteProduct(userContext, productId);
  }

}