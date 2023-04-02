package app.services.order;

import app.exceptions.BaseException;

public class OrderException extends BaseException {
    public static final String ORDER_NOT_FOUND = "Order Not Found";
    public static final String ORDER_ALREADY_EXISTS = "Order Already Exists";

    public OrderException(String message, int statusCode) {
        super(message, statusCode);
    }
}