package app.services.wishlist.exceptions;

import app.exceptions.BaseException;

public class WishlistException extends BaseException {
  public static String WISHLIST_NOT_FOUND = "Wishlist not found!";
  public WishlistException(String message, int statusCode) {
    super(message, statusCode);
  }
}
