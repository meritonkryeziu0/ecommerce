package app.mongodb;

import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.services.order.models.Order;
import app.services.product.models.Product;
import app.shared.BaseModel;
import app.utils.Utils;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class MongoUtils {
  public static <T> Uni<PaginatedResponse<T>> getPaginatedItems(ReactiveMongoCollection<T> collection, PaginationWrapper paginationFilter) {
    PaginatedResponse<T> page = new PaginatedResponse<>();

    Uni<Long> count = collection.countDocuments(paginationFilter.toBson());
    Uni<List<T>> res = collection.aggregate(Arrays.asList(
        new Document("$match", paginationFilter.toBson()),
        new Document("$match", new Document(Utils.notBlank(paginationFilter.getQ())
            ? new Document(Product.FIELD_NAME, new Document("$regex", paginationFilter.getQ()))
            : new Document())),
        new Document("$sort", (paginationFilter.getSortAscending() != null)
            ? new Document(paginationFilter.getSortAscending(), 1L)
            : ((paginationFilter.getSortDescending() != null)
            ? new Document(paginationFilter.getSortDescending(), -1L)
            : new Document("_id", 1L))),
        new Document("$skip", (paginationFilter.getPage() - 1) * paginationFilter.getLimit()),
        new Document("$limit", paginationFilter.getLimit()))).collect().asList();

    return Uni.combine().all().unis(count, res).combinedWith((countValue, data) -> {
      page.setTotalEntities(countValue.intValue());
      page.setTotalPages((int) Math.ceil((double) countValue / paginationFilter.getLimit()));
      page.setData(data);
      page.setReturnedEntities(data.size());
      page.setCurrentPage(paginationFilter.getPage());
      return page;
    });
  }

  public static <T> Uni<PaginatedResponse<T>> getPaginatedItemsFromList(Uni<List<T>> data, PaginationWrapper paginationFilter) {
    PaginatedResponse<T> page = new PaginatedResponse<>();

    Uni<Integer> count = data.map(List::size);

    return Uni.combine().all().unis(count, data).combinedWith((countValue, res) -> {
      page.setTotalEntities(countValue);
      page.setTotalPages((int) Math.ceil((double) countValue / paginationFilter.getLimit()));
      page.setData(res);
      page.setReturnedEntities(res.size());
      page.setCurrentPage(paginationFilter.getPage());
      return page;
    });
  }
  public static <E extends BaseModel> Uni<E> addEntity(E entity) {
    entity.setId(new ObjectId().toString());
    entity.setCreatedAt(LocalDateTime.now());
    entity.setModifiedAt(LocalDateTime.now());
    return entity.persist();
  }

  public static <E extends BaseModel> Uni<E> addEntity(ClientSession session, ReactiveMongoCollection<E> collection, E entity) {
    entity.setId(new ObjectId().toString());
    entity.setCreatedAt(LocalDateTime.now());
    entity.setModifiedAt(LocalDateTime.now());
    return collection.insertOne(session, entity).map(insertOneResult -> entity);
  }

  public static <E extends BaseModel> Uni<E> updateEntity(E entity) {
    entity.setModifiedAt(LocalDateTime.now());
    return entity.update();
  }

  public static <E extends BaseModel> Uni<E> updateEntity(ReactiveMongoCollection<E> collection, Bson filter, Bson update) {
    Bson modifiedAt = Updates.set(Order.FIELD_MODIFIED_AT, LocalDateTime.now());
    Bson updates = Updates.combine(modifiedAt, update);
    return collection.findOneAndUpdate(filter, updates);
  }

  public static <E extends BaseModel> Uni<E> updateEntity(ClientSession session, ReactiveMongoCollection<E> collection, Bson filter, E entity) {
    entity.setModifiedAt(LocalDateTime.now());
    return collection.findOneAndReplace(session, filter, entity, new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER));
  }
}