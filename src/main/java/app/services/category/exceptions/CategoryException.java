package app.services.category.exceptions;

import app.exceptions.BaseException;

import javax.ws.rs.core.Response;

public class CategoryException extends BaseException {
  public static String CATEGORY_NOT_FOUND = "Category not found";
  public static String PARENT_CATEGORY_NOT_FOUND = "Parent Category not found";
  public static String CATEGORY_CANNOT_BE_DELETED = "Category either contains products or is a main category and cannot be deleted";

  public CategoryException() {}

  public CategoryException(String message, int statusCode) {
    super(message, statusCode);
  }

  public CategoryException(String message, Response.Status status) {
    super(message, status);
  }
}
