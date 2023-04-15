package app.services.manufacturer.exceptions;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class ManufacturerException extends BaseException {

  public static final String MANUFACTURER_NOT_FOUND = "Manufacturer Not Found";
  public static final String MANUFACTURER_HAS_PRODUCTS = "Manufacturer has products and cannot be deleted";

  public ManufacturerException(String message, Response.Status statusCode) {
    super(message, statusCode);
  }
}
