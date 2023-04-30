package app.services.category.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateCategory {
  @NotBlank
  @Size(max = 250)
  private String name;
  @Size(max = 1000)
  private String description;
  @NotNull
  private boolean subcategory;
  private String parentCategoryName;
}
