package app.exceptions;


import io.vertx.core.json.JsonObject;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.core.Response;

public class ExceptionMapper {
  @ServerExceptionMapper
  public Response mapException(BaseException baseException) {
    JsonObject reduced = JsonObject.of("message", baseException.getMessage(), "statusCode", baseException.getStatusCode());
    return Response.status(baseException.getStatusCode()).entity(reduced).build();
  }

  @ServerExceptionMapper
  public Response mapException(ValidationException validationException) {
    JsonObject reduced = JsonObject.of("code", validationException.getCode(), "message",
        validationException.getMessage(), "violations", validationException.getErrorMessages());
    return Response.status(validationException.getCode()).entity(reduced).build();
  }
}

