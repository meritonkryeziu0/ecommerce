package app.services.category;

import app.services.category.models.Category;
import app.services.category.models.CategoryType;
import app.services.category.models.CreateCategory;
import app.services.category.models.UpdateCategory;

import java.util.function.Function;

public class CategoryMapper {
  public static Category from(CreateCategory createCategory){
    Category category = new Category();
    category.setName(createCategory.getName());
    category.setDescription(createCategory.getDescription());
    category.setImageUrl(createCategory.getImageUrl());

    if(createCategory.isSubcategory()){
      category.setCategoryType(CategoryType.SUBCATEGORY);
      category.setParentCategoryId(createCategory.getParentCategoryId());
    } else{
      category.setCategoryType(CategoryType.MAIN_CATEGORY);
    }

    return category;
  }

  public static Function<Category, Category> from(UpdateCategory updateCategory){
    return category -> {
      category.setDescription(updateCategory.getDescription());
      category.setImageUrl(updateCategory.getImageUrl());
      return category;
    };
  }
}
