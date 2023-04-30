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
import app.utils.Utils;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.function.Function;

@ApplicationScoped
public class CategoryService {
  @Inject
  CustomValidator validator;
  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;

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

  public Uni<Category> add(CreateCategory createCategory) {
    return validator.validate(createCategory)
        .map(CategoryMapper.INSTANCE::from)
        .call(category -> {
          if(createCategory.isSubcategory()){
            return this.getById(category.getParentCategoryId())
                .onItem().ifNull()
                .failWith(new CategoryException(CategoryException.PARENT_CATEGORY_NOT_FOUND, Response.Status.BAD_REQUEST));
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
    return Category.deleteById(id).replaceWithVoid();
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
