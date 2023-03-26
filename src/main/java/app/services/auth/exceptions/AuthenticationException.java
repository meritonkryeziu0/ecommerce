package app.services.auth.exceptions;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class AuthenticationException extends BaseException {

  public AuthenticationException(String message, Response.Status status) {
    super(message, status);
  }

  public static class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException(String message) {
      super(message, Response.Status.BAD_REQUEST);
    }
  }
}
