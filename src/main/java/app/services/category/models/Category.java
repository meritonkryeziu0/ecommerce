package app.services.category.models;

import app.shared.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseModel {
  public static String FIELD_NAME = "name";
  private String name;
  private String description;
  private String imageUrl;
  private CategoryType categoryType;
  private String parentCategoryId;
}
