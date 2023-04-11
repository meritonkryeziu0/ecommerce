package app.services.category;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoUtils;
import app.services.category.exceptions.CategoryException;
import app.services.category.models.Category;
import app.services.category.models.CategoryReference;
import app.services.category.models.CreateCategory;
import app.services.category.models.UpdateCategory;
import app.services.product.models.Product;
import app.services.wishlist.exceptions.WishlistException;
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
  CategoryRepository repository;

  public Uni<PaginatedResponse<Category>> getList(PaginationWrapper wrapper) {
    return MongoUtils.getPaginatedItems(repository.getCollection(), wrapper);
  }

  public Uni<Category> getById(String id) {
    return repository.getById(id).onItem().ifNull().failWith(new CategoryException(CategoryException.CATEGORY_NOT_FOUND, Response.Status.NOT_FOUND));
  }

  public Uni<Category> add(CreateCategory createCategory) {
    return validator.validate(createCategory)
        .replaceWith(CategoryMapper.from(createCategory))
        .flatMap(category -> repository.add(category));
  }

  public Uni<Category> update(String id, UpdateCategory updateCategory) {
    return validator.validate(updateCategory)
        .replaceWith(getById(id))
        .onFailure().transform(transformToBadRequest(CategoryException.CATEGORY_NOT_FOUND, Response.Status.BAD_REQUEST))
        .map(CategoryMapper.from(updateCategory))
        .flatMap(category -> repository.update(id, category));
  }

  public Uni<Void> delete(String id) {
    return repository.delete(id).replaceWithVoid();
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
