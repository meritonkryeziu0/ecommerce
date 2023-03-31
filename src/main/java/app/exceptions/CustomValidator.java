package app.exceptions;

import app.common.ListWrapper;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;
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

}