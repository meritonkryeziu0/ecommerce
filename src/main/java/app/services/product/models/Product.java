package app.services.product.models;

import app.mongodb.MongoCollections;
import app.services.category.models.CategoryReference;
import app.services.manufacturer.models.ManufacturerReference;
import app.shared.BaseModel;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@MongoEntity(collection = MongoCollections.PRODUCTS_COLLECTION)
public class Product extends BaseModel {
  public static final String FIELD_NAME = "name";
  public static final String FIELD_DESCRIPTION = "description";
  public static final String FIELD_PRICE = "price";
  public static final String FIELD_STOCK_QUANTITY = "stockQuantity";
  public static final String FIELD_DETAILS = "details";
  public static final String FIELD_TYPE = "type";
  public static final String FIELD_MANUFACTURER_NAME = "manufacturer.name";
  public static final String FIELD_MANUFACTURER = "manufacturer";
  public static final String FIELD_MANUFACTURER_ID = "manufacturer._id";


  private String name;
  private Double price;
  private String description;
  private Integer stockQuantity;
  private String type;
  private Map<String, String> details;
  private ManufacturerReference manufacturer;
  private CreateProduct.PackageDetails packageDetails;
  private CategoryReference category;
}
