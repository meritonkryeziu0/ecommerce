package app.services.product;

import app.helpers.PaginationWrapper;
import app.services.product.models.Product;
import app.utils.Utils;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

public class ProductFilterWrapper extends PaginationWrapper {
  @QueryParam("manufacturer-name")
  String manufacturerName;

  @QueryParam("p")
  String priceRange;

  @Override
  public Bson toBson() {
    List<Bson> filters = new ArrayList<>();

    if (Utils.notNull(manufacturerName)) {
      filters.add(Filters.eq(Product.FIELD_MANUFACTURER_NAME, manufacturerName));
    }

    if (Utils.notNull(priceRange)){
      String[] split = priceRange.split("~");
      int lowerBound = "".equals(split[0]) ? 0 : Integer.parseInt(split[0]);
      int higherBound = split.length == 1 ? Integer.MAX_VALUE : Integer.parseInt(split[1]);
      filters.add(Filters.and(
          Filters.gte(Product.FIELD_PRICE, lowerBound),
          Filters.lt(Product.FIELD_PRICE, higherBound)));
    }

    if (filters.isEmpty()) {
      return new Document();
    }

    return Updates.combine(filters);
  }
}
