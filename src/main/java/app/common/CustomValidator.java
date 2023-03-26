package app.common;

import app.exceptions.ValidationException;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class CustomValidator {
  @Inject
  Validator validator;

  public <T> Uni<T> validate(T object) {
    Set<ConstraintViolation<Object>> violations = validator.validate(object);
    if (violations.isEmpty()) {
      return Uni.createFrom().item(object);
    }

    StringBuilder messageConcatonated = new StringBuilder();
    HashMap<String, String> violationMap = new HashMap<>();
    AtomicBoolean flag = new AtomicBoolean(false);
    violations.forEach(constraintViolation -> {
      if (!flag.get()) {
        messageConcatonated.append(constraintViolation.getPropertyPath().toString()).append(" ").append(constraintViolation.getMessage());
        flag.set(true);
      } else {
        messageConcatonated.append(", " + constraintViolation.getPropertyPath().toString()).append(" ").append(constraintViolation.getMessage());
      }
      violationMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
    });
    return Uni.createFrom().failure(new ValidationException(messageConcatonated.toString(), violations, violationMap));
  }

  public <T> Uni<ListWrapper<T>> validateList(List<T> objects) {
    ListWrapper<T> wrapper = new ListWrapper<>(objects);
    return validate(wrapper);
  }

  public static class CustomValidationException extends javax.validation.ValidationException {

    Map<String, String> violations;

    public CustomValidationException(Map<String, String> violations, String message) {
      super(message);
      this.violations = violations;
    }

    public Map<String, String> getViolations() {
      return violations;
    }
  }

  public class ValidListWrapper<T> {

    @Valid
    private final List<T> list;

    public ValidListWrapper(List<T> list) {
      this.list = list;
    }

    public List<T> getList() {
      return list;
    }
  }

}
