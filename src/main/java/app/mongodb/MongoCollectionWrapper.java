package app.mongodb;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MongoCollectionWrapper {
  @Inject
  ReactiveMongoClient mongoClient;
  @ConfigProperty(name = "quarkus.mongodb.database")
  String database;

  public <T> ReactiveMongoCollection<T> getCollection(String collectionName, Class<T> clazz) {
    return mongoClient.getDatabase(database).getCollection(collectionName, clazz);
  }
}
