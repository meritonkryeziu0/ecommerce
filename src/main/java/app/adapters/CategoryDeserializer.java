package app.adapters;

import app.services.category.models.Category;
import app.services.category.models.MainCategory;
import app.services.category.models.Subcategory;
import app.utils.Utils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class CategoryDeserializer extends StdDeserializer<Category> {
  protected CategoryDeserializer(Class<?> vc) {
    super(vc);
  }

  protected CategoryDeserializer(JavaType valueType) {
    super(valueType);
  }

  protected CategoryDeserializer(StdDeserializer<?> src) {
    super(src);
  }

  @Override
  public Category deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    String parentCategoryId = node.get(Category.FIELD_PARENT_CATEGORY_ID).asText();

    if (Utils.isBlank(parentCategoryId)) {
      return jp.getCodec().treeToValue(node, MainCategory.class);
    } else if (Utils.notBlank(parentCategoryId)) {
      return jp.getCodec().treeToValue(node, Subcategory.class);
    } else {
      throw new IllegalArgumentException("Unknown category type!");
    }
  }
}