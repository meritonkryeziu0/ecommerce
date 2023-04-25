package app.services.order;

import app.helpers.PaginatedResponse;
import app.services.accounts.models.Roles;
import app.services.authorization.ability.ActionAbility;
import app.services.authorization.roles.Operation;
import app.services.authorization.roles.Modules;
import app.services.order.models.Order;
import app.services.order.models.UpdateOrder;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
  @Inject
  OrderService service;

  @GET
  @ActionAbility(
      role = {
          Roles.USER,
          Roles.ADMIN
      },
      action = Operation.LIST, module = Modules.Order)
  public Uni<PaginatedResponse<Order>> getList(@BeanParam OrderFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @GET
  @Path("/{id}")
  @ActionAbility(
      role = {
          Roles.USER,
          Roles.ADMIN
      },
      action = Operation.READ, module = Modules.Order)
  public Uni<Order> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(
      role = {
          Roles.ADMIN
      },
      action = Operation.UPDATE, module = Modules.Order)
  public Uni<Order> update(@PathParam("id") String id, UpdateOrder updateOrder) {
    return service.update(id, updateOrder);
  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(
      role = {
          Roles.ADMIN
      },
      action = Operation.DELETE, module = Modules.Order)
  public Uni<SuccessResponse> delete(@PathParam("id") String id) {
    return service.delete(id);
  }
}