package app.services.category;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.category.exceptions.CategoryException;
import app.services.category.models.Category;
import app.services.category.models.CreateCategory;
import app.services.category.models.UpdateCategory;
import app.services.product.ProductService;
import app.services.product.models.Product;
import app.utils.Utils;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class CategoryService {
  @Inject
  CustomValidator validator;
  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;

  @Inject
  ProductService productService;

  public ReactiveMongoCollection<Category> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.CATEGORY_COLLECTION, Category.class);
  }
  public Uni<PaginatedResponse<Category>> getList(PaginationWrapper wrapper) {
    return MongoUtils.getPaginatedItems(getCollection(), wrapper);
  }

  public Uni<Category> getById(String id) {
    return Category.findById(id)
        .onItem().ifNull()
        .failWith(new CategoryException(CategoryException.CATEGORY_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(Category.class));
  }

  public Uni<Category> getByName(String name) {
    return Category.find(Category.FIELD_NAME, name)
        .firstResult()
        .onItem().ifNull()
        .failWith(new CategoryException(CategoryException.CATEGORY_NOT_FOUND, Response.Status.NOT_FOUND))
        .map(Utils.mapTo(Category.class));
  }

  public Uni<List<Category>> getListByParentCategoryName(String parentCategoryName){
      return Category.find(Category.FIELD_PARENT_CATEGORY_NAME, parentCategoryName).list();
  }

  public Uni<Category> add(CreateCategory createCategory) {
    return validator.validate(createCategory)
        .map(CategoryMapper.INSTANCE::from)
        .flatMap(category -> {
          if(createCategory.isSubcategory()){
            return this.getByName(category.getParentCategoryName())
                .onFailure().transform(transformToBadRequest(CategoryException.PARENT_CATEGORY_NOT_FOUND, Response.Status.BAD_REQUEST))
                .map(subcategory -> category);
          }
          return Uni.createFrom().item(category);
        })
        .call(MongoUtils::addEntity);
  }

  public Uni<Category> update(String id, UpdateCategory updateCategory) {
    return validator.validate(updateCategory)
        .replaceWith(getById(id))
        .onFailure().transform(transformToBadRequest(CategoryException.CATEGORY_NOT_FOUND, Response.Status.BAD_REQUEST))
        .map(CategoryMapper.from(updateCategory))
        .call(MongoUtils::updateEntity);
  }

  public Uni<Void> delete(String id) {
    return this.getById(id)
        .call(this::checkIfParentCategory)
        .call(this::checkIfContainsProducts)
        .onFailure().transform(transformToBadRequest(CategoryException.CATEGORY_CANNOT_BE_DELETED, Response.Status.BAD_REQUEST))
        .flatMap(category -> Category.deleteById(id)).replaceWithVoid();
  }

  private Uni<List<Category>> checkIfParentCategory(Category category) {
    return this.getListByParentCategoryName(category.getName())
        .flatMap(categories -> {
          if (categories.size() == 0) {
            return Uni.createFrom().item(categories);
          }
          return Uni.createFrom().failure(new CategoryException());
        });
  }

  private Uni<List<Product>> checkIfContainsProducts(Category category) {
    return productService.getListByCategoryId(category.getId())
        .flatMap(products -> {
          if (products.size() == 0) {
            return Uni.createFrom().item(products);
          }
          return Uni.createFrom().failure(new CategoryException());
        });
  }

  private static Function<Throwable, Throwable> transformToBadRequest(String message, Response.Status status) {
    return throwable -> {
      if (throwable instanceof BaseException) {
        return new CategoryException(message, status);
      }
      return throwable;
    };
  }
}
