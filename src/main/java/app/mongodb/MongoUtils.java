package app.mongodb;

//import app.services.product.models.Product;

import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.shared.BaseModel;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
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
    /*public static <T> Uni<PaginatedResponse<T>> getPaginatedItems(ReactiveMongoCollection<T> collection, PaginationWrapper paginationFilter) {
        PaginatedResponse<T> page = new PaginatedResponse<>();

        Uni<Long> count = collection.countDocuments(paginationFilter.toBson());
        Uni<List<T>> res = collection.aggregate(Arrays.asList(new Document("$match", paginationFilter.toBson()),
                new Document("$match", new Document(paginationFilter.getQ() != null
                        ? new Document(Product.FIELD_NAME, new Document("$regex", paginationFilter.getQ()))
                        : new Document(Product.FIELD_NAME, new Document("$regex", "")))),
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
    }*/

    public static <T> Uni<PaginatedResponse<T>> getPaginatedItems(ReactiveMongoCollection<T> collection, PaginationWrapper paginationFilter) {
        PaginatedResponse<T> page = new PaginatedResponse<>();

        Uni<Long> count = collection.countDocuments(paginationFilter.toBson());
        Uni<List<T>> res = collection.aggregate(Arrays.asList(new Document("$match", paginationFilter.toBson()),
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

    public static <E extends BaseModel> Uni<E> addEntity(ReactiveMongoCollection<E> collection, E entity) {
        entity.setId(new ObjectId().toString());

        entity.setCreatedAt(LocalDateTime.now());
        entity.setModifiedAt(LocalDateTime.now());
        return collection.insertOne(entity).map(insertOneResult -> entity);
    }

    public static <E extends BaseModel> Uni<E> addEntity(ClientSession session, ReactiveMongoCollection<E> collection, E entity) {
        entity.setId(new ObjectId().toString());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setModifiedAt(LocalDateTime.now());
        return collection.insertOne(session, entity).map(insertOneResult -> entity);
    }

    public static <E extends BaseModel> Uni<E> updateEntity(ReactiveMongoCollection<E> collection, Bson filter, E entity) {
        entity.setModifiedAt(LocalDateTime.now());
        return collection.findOneAndReplace(filter, entity, new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER));
    }

    public static <E extends BaseModel> Uni<E> updateEntity(ClientSession session, ReactiveMongoCollection<E> collection, Bson filter, E entity) {
        entity.setModifiedAt(LocalDateTime.now());
        return collection.findOneAndReplace(session, filter, entity, new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER));
    }
}