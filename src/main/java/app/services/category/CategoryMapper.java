package app.services.category;

import app.services.category.models.*;
import app.utils.Utils;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.function.Function;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

  CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

  default Category from(CreateCategory createCategory) {
    Category category;

    if (createCategory.isSubcategory()) {
      category = new Subcategory();
    } else {
      category = new MainCategory();
    }

    category.setName(createCategory.getName());
    category.setDescription(createCategory.getDescription());

    if (category instanceof Subcategory && Utils.notBlank(createCategory.getParentCategoryId())) {
      category.setParentCategoryId(createCategory.getParentCategoryId());
      category.setCategoryType(CategoryType.SUBCATEGORY);
    } else {
      category.setCategoryType(CategoryType.MAIN_CATEGORY);
    }

    return category;
  }

  CategoryReference toCategoryReference(Category category);

  static Function<Category, Category> from(UpdateCategory updateCategory) {
    return category -> {
      category.setDescription(updateCategory.getDescription());
      return category;
    };
  }
}
