package app.services.order;

import app.helpers.PaginatedResponse;
import app.services.order.models.Order;
import app.services.order.models.UpdateOrder;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/order")
public class OrderResource {
    @Inject
    OrderService service;

    @GET
    public Uni<PaginatedResponse<Order>> getList(@BeanParam OrderFilterWrapper wrapper) {
        return service.getList(wrapper);
    }

    @GET
    @Path("/{id}")
    public Uni<Order> getById(@PathParam("id") String id) {
        return service.getById(id);
    }

    @PUT
    @Path("/{id}")
    public Uni<Order> update(@PathParam("id") String id, UpdateOrder updateOrder) {
        return service.update(id, updateOrder);
    }

    @DELETE
    @Path("/{id}")
    public Uni<SuccessResponse> delete(@PathParam("id") String id) {
        return service.delete(id);
    }
}