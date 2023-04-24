package app.services.manufacturer;

import app.services.manufacturer.models.CreateManufacturer;
import app.services.manufacturer.models.Manufacturer;
import app.services.manufacturer.models.UpdateManufacturer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.function.Function;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ManufacturerMapper {
  ManufacturerMapper INSTANCE = Mappers.getMapper(ManufacturerMapper.class);

  Manufacturer from(CreateManufacturer createManufacturer);

  public static Function<Manufacturer, Manufacturer> from(UpdateManufacturer updateManufacturer) {
    return manufacture -> {
      manufacture.setName(updateManufacturer.getName());
      manufacture.setAddress(updateManufacturer.getAddress());
      return manufacture;
    };
  }
}