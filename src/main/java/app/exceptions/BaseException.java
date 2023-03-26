package app.exceptions;

import lombok.Getter;

import javax.ws.rs.core.Response.Status;

@Getter
public class BaseException extends Exception {
  private String message;
  private int statusCode;
  private Status status;

  public BaseException(String message, int statusCode) {
    this.message = message;
    this.statusCode = statusCode;
    this.status = Status.fromStatusCode(statusCode);
  }

  public BaseException(String message, Status status){
    this.message = message;
    this.status = status;
    this.statusCode = status.getStatusCode();
  }
}
