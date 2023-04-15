package app.services.product.exceptions;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class ProductException extends BaseException {

  public static final String PRODUCT_NOT_FOUND = "Product Not Found";
  public static final String NOT_ENOUGH_STOCK = "Product Already Exists";

  public ProductException(String message, Response.Status statusCode) {
    super(message, statusCode);
  }
}
