package app.services.roles;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class RolesException extends BaseException {
  public static final String ROLE_NOT_FOUND = "Role not found!";
  public RolesException(String message, int statusCode) {
    super(message, statusCode);
  }

  public RolesException(String message, Response.Status status) {
    super(message, status);
  }
}
