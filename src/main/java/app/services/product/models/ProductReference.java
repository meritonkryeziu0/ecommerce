package app.services.product.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductReference {
  public static final String FIELD_QUANTITY = "quantity";
  public String id;
  private String name;
  private Double price;
  @NotNull
  @Min(value = 1)
  private Integer quantity;
}
