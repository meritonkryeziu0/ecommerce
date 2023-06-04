package app.services.order;

import app.context.UserContext;
import app.helpers.PaginatedResponse;
import app.services.authorization.ability.ActionAbility;
import app.services.order.models.Order;
import app.services.order.models.UpdateOrder;
import app.services.order.models.UpdateOrderStatus;
import app.services.product.models.Rating;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.shared.BaseAddress;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
  @Inject
  OrderService service;

  @Inject
  UserContext userContext;

  @GET
  @ActionAbility(action = Actions.LIST, module = Modules.Order)
  public Uni<PaginatedResponse<Order>> getList(@BeanParam OrderFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @GET
  @Path("my-orders")
  @ActionAbility(action = Actions.LIST, module = Modules.Order)
  public Uni<List<Order>> getUserOrders() {
    return service.getList(userContext.getId());
  }

  @GET
  @Path("/{id}")
  @ActionAbility(action = Actions.READ, module = Modules.Order)
  public Uni<Order> getById(@PathParam("id") String id) {
    return service.getById(userContext.getId(), id);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Order)
  public Uni<Order> update(@PathParam("id") String id, UpdateOrder updateOrder) {
    return service.update(id, updateOrder);
  }

  @PUT
  @Path("/{id}/shipping-address/")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Order)
  public Uni<Order> editShippingAddress(@PathParam("id") String id, BaseAddress shippingAddress) {
    return service.editShippingAddress(id, shippingAddress);
  }


  @DELETE
  @Path("/{id}/cancel")
  @ActionAbility(action = Actions.CANCEL, module = Modules.Order)
  public Uni<SuccessResponse> cancelOrder(@PathParam("id") String id){
    return service.cancelOrder(userContext.getId(), id);
  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(action = Actions.DELETE, module = Modules.Order)
  public Uni<SuccessResponse> delete(@PathParam("id") String id) {
    return service.delete(id);
  }


  @PUT
  @Path("/{id}/update-status")
  @ActionAbility(action = Actions.UPDATE, module = Modules.OrderSatus)
  public Uni<SuccessResponse> updateStatus(@PathParam("id") String id, UpdateOrderStatus status){
    return service.updateStatus(id, status);
  }

  @POST
  @Path("/{id}/products/{productId}/rate")
  @ActionAbility(action = Actions.CREATE, module = Modules.Order)
  public Uni<SuccessResponse> rateProduct(@PathParam("id") String id, @PathParam("productId") String productId, Rating rating) {
    return service.rateProduct(id, productId, rating).map(unused -> SuccessResponse.toSuccessResponse());
  }

}