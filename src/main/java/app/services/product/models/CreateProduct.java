package app.services.product.models;

import app.services.manufacturer.models.ManufacturerReference;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Map;

@Data
public class CreateProduct {
  @NotBlank
  @Size(max = 250)
  private String name;
  @Size(max = 1000)
  private String description;
  @NotNull
  @DecimalMin(value = "0.0")
  private Double price;
  @NotNull
  @Min(value = 1)
  private Integer stockQuantity;
  @NotBlank
  private String type;
  @Valid
  private ManufacturerReference manufacturer;
  private Map<String, String> details;
  @Valid
  private PackageDetails packageDetails;

  @Data
  public static class PackageDetails {
    @NotNull
    @Min(value = 0)
    private Double weight;
    @NotNull
    @Min(value = 0)
    private Double height;
    @NotNull
    @Min(value = 0)
    private Double width;
    @NotNull
    @Min(value = 0)
    private Double depth;
  }
}
