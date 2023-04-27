package app.services.manufacturer;

import app.helpers.PaginatedResponse;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.Operation;
import app.services.roles.Modules;
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

  @GET
  @ActionAbility(action = Operation.LIST, module = Modules.Manufacturer)
  public Uni<PaginatedResponse<Manufacturer>> getList(@BeanParam ManufacturerFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @GET
  @Path("/{id}")
  @ActionAbility(action = Operation.READ, module = Modules.Manufacturer)
  public Uni<Manufacturer> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @POST
  @ActionAbility(action = Operation.CREATE, module = Modules.Manufacturer)
  public Uni<Manufacturer> add(CreateManufacturer createManufacturer) {
    return service.add(createManufacturer);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(action = Operation.UPDATE, module = Modules.Manufacturer)
  public Uni<Manufacturer> update(@PathParam("id") String id, UpdateManufacturer updateManufacturer) {
    return service.update(id, updateManufacturer);
  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(action = Operation.DELETE, module = Modules.Manufacturer)
  public Uni<SuccessResponse> delete(@PathParam("id") String id) {
    return service.delete(id);
  }

}
