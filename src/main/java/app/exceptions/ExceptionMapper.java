package app.exceptions;


import app.common.CustomValidator;
import io.vertx.core.json.JsonObject;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.core.Response;

public class ExceptionMapper {
    @ServerExceptionMapper
    public Response mapException(BaseException baseException) {
        JsonObject reduced = JsonObject.of(
            "message", baseException.getMessage(),
            "code", baseException.getStatusCode());
        return Response.status(baseException.getStatusCode()).entity(reduced).build();
    }

    @ServerExceptionMapper
    public Response mapException(CustomValidator.CustomValidationException validationException) {
        JsonObject reduced = JsonObject.of(
            "message", validationException.getMessage(),
            "violations", validationException.getViolations());
        return Response.status(Response.Status.BAD_REQUEST).entity(reduced).build();
    }

}

