package app.services.shoppingcart.exceptions;


import app.exceptions.BaseException;

public class ShoppingCartException extends BaseException {
  public static final String PRODUCT_NOT_ADDED = "Cannot add product to cart!";
  public static final String SHOPPING_CART_NOT_FOUND = "Shopping Cart Not Found!";

  public ShoppingCartException(String message, int statusCode) {
    super(message, statusCode);
  }
}
