package app.adapters;

import app.services.category.models.Category;
import app.services.category.models.CategoryType;
import app.services.category.models.Subcategory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class CategorySerializer extends StdSerializer<Category> {

  public CategorySerializer() {
    this(null);
  }

  public CategorySerializer(Class<Category> t) {
    super(t);
  }

  @Override
  public void serialize(Category category, JsonGenerator jgen, SerializerProvider provider) throws IOException {
    jgen.writeStartObject();
    jgen.writeStringField(Category.FIELD_ID, category.getId());
    jgen.writeStringField(Category.FIELD_CREATED_AT, category.getCreatedAt().toString());
    jgen.writeStringField(Category.FIELD_MODIFIED_AT, category.getModifiedAt().toString());
    jgen.writeStringField(Category.FIELD_NAME, category.getName());
    jgen.writeStringField(Category.FIELD_DESCRIPTION, category.getDescription());
    jgen.writeStringField(Category.FIELD_CATEGORY_TYPE, category.getCategoryType().name());

    if (category.getCategoryType().equals(CategoryType.SUBCATEGORY)) {
      jgen.writeStringField(Category.FIELD_PARENT_CATEGORY_ID, category.getParentCategoryId());
    }

    jgen.writeEndObject();
  }
}