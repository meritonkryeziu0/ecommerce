package app.services.category.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateCategory {
  @Size(max = 1000)
  private String description;
}
