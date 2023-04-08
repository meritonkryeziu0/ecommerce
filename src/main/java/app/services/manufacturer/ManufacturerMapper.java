package app.services.manufacturer;

import app.services.manufacturer.models.CreateManufacturer;
import app.services.manufacturer.models.Manufacturer;
import app.services.manufacturer.models.UpdateManufacturer;

import java.util.function.Function;

public class ManufacturerMapper {
  public static Manufacturer from(CreateManufacturer createManufacturer) {
    Manufacturer manufacturer = new Manufacturer();
    manufacturer.setName(createManufacturer.getName());
    manufacturer.setAddress(createManufacturer.getAddress());
    return manufacturer;
  }

  public static Function<Manufacturer, Manufacturer> from(UpdateManufacturer updateManufacturer) {
    return manufacture -> {
      manufacture.setName(updateManufacturer.getName());
      manufacture.setAddress(updateManufacturer.getAddress());
      return manufacture;
    };
  }
}