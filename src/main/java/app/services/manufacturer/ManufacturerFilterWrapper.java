package app.services.manufacturer;

import app.helpers.PaginationWrapper;
import app.services.manufacturer.models.Manufacturer;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

public class ManufacturerFilterWrapper extends PaginationWrapper {
  @QueryParam("name")
  String name;

  @Override
  public Bson toBson() {
    List<Bson> filters = new ArrayList<>();

    if (name != null) {
      filters.add(Filters.eq(Manufacturer.FIELD_NAME, name));
    }

    if (filters.isEmpty()) {
      return new Document();
    }

    return Updates.combine(filters);
  }
}
