package app.services.promotion;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class PromotedProductException extends BaseException {
  public static final String PRODUCT_ALREADY_PROMOTED = "Product already promoted";
  public PromotedProductException(String message, Response.Status statusCode) {
    super(message, statusCode);
  }
}
