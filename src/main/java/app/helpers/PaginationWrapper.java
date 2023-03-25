package app.helpers;

import lombok.Getter;
import org.bson.conversions.Bson;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

@Getter
public abstract class PaginationWrapper {
    @QueryParam("q")
    String q;
    @QueryParam("page")
    @DefaultValue("1")
    int page;
    @QueryParam("limit")
    @DefaultValue("10")
    int limit;
    @QueryParam("asc")
    String sortAscending;
    @QueryParam("dsc")
    String sortDescending;

    public abstract Bson toBson();
}