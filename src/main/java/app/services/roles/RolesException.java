package app.services.roles;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class RolesException extends BaseException {
  public static final String ROLE_NOT_FOUND = "Role not found!";
  public static final String ROLE_ALREADY_EXISTS = "Role already exists!";
  public RolesException(String message, int statusCode) {
    super(message, statusCode);
  }

  public RolesException(String message, Response.Status status) {
    super(message, status);
  }
}
