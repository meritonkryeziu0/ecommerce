package app.services.category;

import app.services.category.models.Category;
import app.services.category.models.CategoryReference;
import app.services.category.models.CreateCategory;
import app.services.category.models.UpdateCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.function.Function;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

  CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

  @Mapping(target = "categoryType", expression = "java(createCategory.isSubcategory() ? app.services.category.models.CategoryType.SUBCATEGORY : app.services.category.models.CategoryType.MAIN_CATEGORY)")
  @Mapping(target = "parentCategoryId", source = "createCategory.parentCategoryId", conditionQualifiedByName = "isSubcategory")
  Category from(CreateCategory createCategory);


  @Named("isSubcategory")
  default boolean isSubcategory(CreateCategory createCategory) {
    return createCategory.isSubcategory();
  }

  CategoryReference toCategoryReference(Category category);


  static Function<Category, Category> from(UpdateCategory updateCategory){
    return category -> {
      category.setDescription(updateCategory.getDescription());
      category.setImageUrl(updateCategory.getImageUrl());
      return category;
    };
  }
}
