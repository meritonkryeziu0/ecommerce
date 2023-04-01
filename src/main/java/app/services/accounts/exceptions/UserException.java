package app.services.accounts.exceptions;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class UserException extends BaseException {
  public static final String USER_NOT_FOUND = "User Not Found";
  public static final String USER_ALREADY_EXISTS = "User Already Exists";

  public UserException(String message, Response.Status status) {
    super(message, status);
  }
}
