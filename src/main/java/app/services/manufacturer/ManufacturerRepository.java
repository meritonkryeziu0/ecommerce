package app.services.manufacturer;

import app.mongodb.MongoUtils;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.services.manufacturer.models.Manufacturer;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ManufacturerRepository {
    @Inject
    MongoCollectionWrapper mongoClient;

    public ReactiveMongoCollection<Manufacturer> getCollection() {
        return mongoClient.getCollection(MongoCollections.MANUFACTURERS_COLLECTION, Manufacturer.class);
    }

    public Uni<Manufacturer> add(Manufacturer manufacturer) {
        return MongoUtils.add(getCollection(), manufacturer);
    }

    public Uni<Manufacturer> getById(String id) {
        return getCollection().find(Filters.eq(Manufacturer.FIELD_ID, id)).toUni();
    }

    public Uni<DeleteResult> delete(String id) {
        return getCollection().deleteOne(Filters.eq(Manufacturer.FIELD_ID, id));
    }

    public Uni<Manufacturer> update(ClientSession session, String id, Manufacturer manufacturer) {
        return MongoUtils.update(session, getCollection(), Filters.eq(Manufacturer.FIELD_ID, id), manufacturer);
    }

}
