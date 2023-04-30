package app.services.category.models;

import app.mongodb.MongoCollections;
import app.shared.BaseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category extends BaseModel {
  public static String FIELD_NAME = "name";
  public static String FIELD_DESCRIPTION = "description";
  public static String FIELD_CATEGORY_TYPE = "categoryType";
  public static String FIELD_PARENT_CATEGORY_ID = "parentCategoryId";
  private String name;
  private String description;
  private CategoryType categoryType;
  private String parentCategoryId;
}