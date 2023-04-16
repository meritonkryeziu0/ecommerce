package app.services.category.models;

import app.mongodb.MongoCollections;
import app.shared.BaseModel;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = MongoCollections.CATEGORY_COLLECTION)
public class Category extends BaseModel {
  public static String FIELD_NAME = "name";
  private String name;
  private String description;
  private String imageUrl;
  private CategoryType categoryType;
  private String parentCategoryId;
}
