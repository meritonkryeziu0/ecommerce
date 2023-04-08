package app.services.wishlist.exceptions;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class WishlistException extends BaseException {
  public static String WISHLIST_NOT_FOUND = "Wishlist not found!";
  public static String PRODUCT_NOT_ADDED = "Product not added!";
  public static String WISHLIST_NOT_CLEARED = "Wishlist not cleared!";
  public WishlistException(String message, Response.Status statusCode) {
    super(message, statusCode);
  }
}
