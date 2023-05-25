package app.services.manufacturer;

import app.helpers.PaginatedResponse;
import app.services.authorization.ability.ActionAbility;
import app.services.roles.models.Actions;
import app.services.roles.models.Modules;
import app.services.manufacturer.models.CreateManufacturer;
import app.services.manufacturer.models.Manufacturer;
import app.services.manufacturer.models.UpdateManufacturer;
import app.shared.SuccessResponse;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/manufacturer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ManufacturerResource {

  @Inject
  ManufacturerService service;

  @GET
  @ActionAbility(action = Actions.LIST, module = Modules.Manufacturer)
  public Uni<PaginatedResponse<Manufacturer>> getList(@BeanParam ManufacturerFilterWrapper wrapper) {
    return service.getList(wrapper);
  }

  @GET
  @Path("/{id}")
  @ActionAbility(action = Actions.READ, module = Modules.Manufacturer)
  public Uni<Manufacturer> getById(@PathParam("id") String id) {
    return service.getById(id);
  }

  @POST
  @ActionAbility(action = Actions.CREATE, module = Modules.Manufacturer)
  public Uni<Manufacturer> add(CreateManufacturer createManufacturer) {
    return service.add(createManufacturer);
  }

  @PUT
  @Path("/{id}")
  @ActionAbility(action = Actions.UPDATE, module = Modules.Manufacturer)
  public Uni<Manufacturer> update(@PathParam("id") String id, UpdateManufacturer updateManufacturer) {
    return service.update(id, updateManufacturer);
  }

  @DELETE
  @Path("/{id}")
  @ActionAbility(action = Actions.DELETE, module = Modules.Manufacturer)
  public Uni<SuccessResponse> delete(@PathParam("id") String id) {
    return service.delete(id);
  }
}
