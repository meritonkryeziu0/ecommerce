package app.services.order;

import app.helpers.PaginationWrapper;
import app.services.accounts.models.User;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

import static app.utils.Utils.notBlank;

public class OrderFilterWrapper extends PaginationWrapper {
    @QueryParam("firstName")
    String firstName;

    @Override
    public Bson toBson() {
        List<Bson> filters = new ArrayList<>();

        if (notBlank(firstName)) {
            filters.add(Filters.eq(User.FIELD_FIRSTNAME, firstName));
        }

        if (filters.isEmpty()) {
            return new Document();
        }

        return Updates.combine(filters);
    }
}