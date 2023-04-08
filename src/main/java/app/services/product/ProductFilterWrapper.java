package app.services.product;

import app.helpers.PaginationWrapper;
import app.services.product.models.Product;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

public class ProductFilterWrapper extends PaginationWrapper {
  @QueryParam("type")
  String type;
  @QueryParam("manufacturer-name")
  String manufacturerName;

  @Override
  public Bson toBson() {
    List<Bson> filters = new ArrayList<>();

    if (type != null) {
      filters.add(Filters.eq(Product.FIELD_TYPE, type));
    }

    if (manufacturerName != null) {
      filters.add(Filters.eq(Product.FIELD_MANUFACTURER_NAME, manufacturerName));
    }

    if (filters.isEmpty()) {
      return new Document();
    }

    return Updates.combine(filters);
  }
}
