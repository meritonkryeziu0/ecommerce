package app.services.order.exceptions;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class OrderException extends BaseException {
  public static final String ORDER_NOT_FOUND = "Order Not Found";
  public static final String ORDER_ALREADY_EXISTS = "Order Already Exists";

  public OrderException(String message, Response.Status statusCode) {
    super(message, statusCode);
  }
}