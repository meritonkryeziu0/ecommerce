package app.services.authorization;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class AuthorizationException extends BaseException {

  public static final String USERCONTEX_ROLE_NOT_SET = "UserContext role not set";
  @Override
  public String getMessage() {
    return super.getMessage();
  }

  @Override
  public int getStatusCode() {
    return super.getStatusCode();
  }

  @Override
  public Response.Status getStatus() {
    return super.getStatus();
  }

  public AuthorizationException(String message, int statusCode) {
    super(message, statusCode);
  }

  public AuthorizationException(String message, Response.Status status) {
    super(message, status);
  }
}
