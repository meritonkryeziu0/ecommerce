package app.services.product.models;

import app.services.manufacturer.models.ManufacturerReference;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

@Getter
@Setter
public class UpdateProduct {
  @NotBlank
  @Size(max = 255)
  private String name;
  @Size(max = 1000)
  private String description;
  @DecimalMin(value = "0.0")
  private Double price;
  @Min(value = 1)
  private Integer stockQuantity;
  private ManufacturerReference manufacturer;
  private Map<String, String> details;
}
