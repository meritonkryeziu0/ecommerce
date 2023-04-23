package app.services.manufacturer.models;

import app.mongodb.MongoCollections;
import app.shared.BaseAddress;
import app.shared.BaseModel;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MongoEntity(collection = MongoCollections.MANUFACTURERS_COLLECTION)
public class Manufacturer extends BaseModel {
  public static String FIELD_ID = "_id";
  public static String FIELD_NAME = "name";

  private String name;
  private BaseAddress address;

}