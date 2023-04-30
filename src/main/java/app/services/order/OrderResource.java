package app.services.order;

import app.helpers.PaginatedResponse;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.services.order.models.Order;
import app.services.order.models.UpdateOrder;
import app.shared.BaseAddress;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/order")
public class OrderResource {
  @Inject
  OrderService service;

  @GET
  @ActionAbility(action = Actions.LIST, module = Modules.Order)
  public Uni<PaginatedResponse<Order>> getList(@BeanParam OrderFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @GET
  @Path("/{id}")
  @ActionAbility(action = Actions.READ, module = Modules.Order)
  public Uni<Order> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Order)
  public Uni<Order> update(@PathParam("id") String id, UpdateOrder updateOrder) {
    return service.update(id, updateOrder);
  }

  @PUT
  @Path("/{id}/shipping-address/")
  public Uni<Order> editShippingAddress(@PathParam("id") String id, BaseAddress shippingAddress) {
    return service.editShippingAddress(id, shippingAddress);
  }


  @DELETE
  @Path("/{id}/cancel")
  public Uni<SuccessResponse> cancelOrder(@PathParam("id") String id){
    return service.cancelOrder(id);
  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(action = Actions.DELETE, module = Modules.Order)
  public Uni<SuccessResponse> delete(@PathParam("id") String id) {
    return service.delete(id);
  }
}