package app.exceptions;

import lombok.Getter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Set;



@Getter
public class ValidationException extends ConstraintViolationException {
  private int code;
  private String message;
  private Map<String, String> errorMessages;

  public <T> ValidationException(String message, Set<ConstraintViolation<T>> violations, Map<String, String> errorMessages) {
    super(message, violations);
    this.code = 400;
    this.message = message;
    this.errorMessages = errorMessages;
  }

}
