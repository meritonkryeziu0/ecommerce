package app.services.order.exceptions;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class OrderException extends BaseException {
  public static final String ORDER_NOT_FOUND = "Order Not Found";
  public static final String ORDER_ALREADY_EXISTS = "Order Already Exists";
  public static final String ORDER_NOT_CANCELLED = "Order cannot be cancelled once DISPATCHED or DELIVERED";
  public static final String ORDER_STATUS_INVALID = "Invalid status order entered!";
  public static final String ORDER_ADDRESS_NOT_CHANGED = "Order shipping address cannot be changed once DISPATCHED or DELIVERED";

  public OrderException(String message, Response.Status statusCode) {
    super(message, statusCode);
  }
}