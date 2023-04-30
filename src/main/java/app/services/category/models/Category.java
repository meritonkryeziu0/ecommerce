package app.services.category.models;

import app.adapters.CategoryDeserializer;
import app.adapters.CategorySerializer;
import app.mongodb.MongoCollections;
import app.shared.BaseModel;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@JsonSerialize(using = CategorySerializer.class)
@JsonDeserialize(using = JsonDeserializer.class)
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