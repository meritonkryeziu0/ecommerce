package app.services.category.models;

import app.mongodb.MongoCollections;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = MongoCollections.CATEGORY_COLLECTION)
public class Subcategory extends Category{
}
