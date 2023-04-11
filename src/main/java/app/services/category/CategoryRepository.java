package app.services.category;

import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoUtils;
import app.services.category.models.Category;
import app.services.category.models.CategoryType;
import app.services.product.models.Product;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CategoryRepository {
  @Inject
  MongoCollectionWrapper mongoCollectionWrapper;
  public ReactiveMongoCollection<Category> getCollection() {
    return mongoCollectionWrapper.getCollection(MongoCollections.CATEGORY_COLLECTION, Category.class);
  }

  public Uni<Category> getById(String id){
    return MongoUtils.getEntityById(getCollection(), id);
  }

  public Uni<Category> getByName(String name){
    return getCollection().find(Filters.eq(Category.FIELD_NAME, name)).toUni();
  }

  public Uni<Category> add(Category category){
    return MongoUtils.addEntity(getCollection(), category);
  }

  public Uni<Category> update(String id, Category category){
    return MongoUtils.updateEntity(getCollection(), Filters.eq(Category.FIELD_ID, id), category);
  }

  public Uni<DeleteResult> delete(String id) {
    return getCollection().deleteOne(Filters.eq(Category.FIELD_ID, id));
  }
}
