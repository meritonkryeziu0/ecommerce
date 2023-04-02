package app.services.product;

import app.helpers.PaginatedResponse;
import app.services.accounts.models.Roles;
import app.services.product.models.CreateProduct;
import app.services.product.models.Product;
import app.services.product.models.UpdateProduct;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/products")
@RolesAllowed({Roles.Fields.Everyone})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {
    @Inject
    ProductService service;

    @GET
    public Uni<PaginatedResponse<Product>> getList(@BeanParam ProductFilterWrapper wrapper) {
        return service.getList(wrapper);
    }

    @GET
    @Path("/{id}")
    public Uni<Product> getById(@PathParam("id") String id) {
        return service.getById(id);
    }

    @POST
    @RolesAllowed({Roles.Fields.Admin})
    public Uni<Product> add(CreateProduct createProduct) {
        return service.add(createProduct);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({Roles.Fields.Admin})
    public Uni<Product> update(@PathParam("id") String id, UpdateProduct updateProduct) {
        return service.update(id, updateProduct);

    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({Roles.Fields.Admin})
    public Uni<Void> delete(@PathParam("id") String id) {
        return service.delete(id);
    }
}