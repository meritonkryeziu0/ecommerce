package app.services.manufacturer.exceptions;

import app.exceptions.BaseException;

public class ManufacturerException extends BaseException {

    public static final String MANUFACTURER_NOT_FOUND = "Manufacturer Not Found";

    public ManufacturerException(String message, int statusCode) {
        super(message, statusCode);
    }
}
