package app.services.manufacturer;

import app.services.manufacturer.models.CreateManufacturer;
import app.services.manufacturer.models.Manufacturer;
import app.services.manufacturer.models.UpdateManufacturer;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/manufacturer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ManufacturerResource {

    @Inject
    ManufacturerService service;

//    @GET
//    public Uni<PaginatedResponse<Manufacturer>> getList(@BeanParam ManufacturerFilterWrapper wrapper) {
//        return service.getList(wrapper);
//    }

//    @GET
//    @Path("/{id}")
//    public Uni<Manufacturer> getById(@PathParam("id") String id) {
//        return service.getById(id);
//    }

    @POST
    public Uni<Manufacturer> add(CreateManufacturer createManufacturer) {
        return service.add(createManufacturer);
    }

//    @PUT
//    @Path("/{id}")
//    public Uni<Manufacturer> update(@PathParam("id") String id, UpdateManufacturer updateManufacturer) {
//        return service.update(id, updateManufacturer);
//    }

//    @DELETE
//    @Path("/{id}")
//    public Uni<SuccessResponse> delete(@PathParam("id") String id) {
//        return service.delete(id);
//    }

}
