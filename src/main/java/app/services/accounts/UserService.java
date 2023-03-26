package app.services.accounts;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.services.accounts.exception.UserException;
import app.services.accounts.models.CreateUser;
import app.utils.PasswordUtils;
import app.utils.Utils;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.function.Function;

@ApplicationScoped
public class UserService {
  @Inject
  CustomValidator validator;
  @Inject
  UserRepository repository;

  public Uni<User> getById(String id) {
    return repository.getById(id)
        .onItem().ifNull().failWith(new UserException(UserException.USER_NOT_FOUND, Response.Status.NOT_FOUND));
  }

  public Uni<User> getWithEmail(String email){
    return repository.getWithEmail(email).onItem().ifNull().failWith(new UserException(UserException.USER_NOT_FOUND, Response.Status.NOT_FOUND));
  }

  public Uni<User> add(CreateUser createUser) {
    return PasswordUtils.validatePassword(createUser.getPassword())
        .replaceWith(validator.validate(createUser))
        .flatMap(verifyUserEmailAndMapToUser())
        .flatMap(user -> repository.add(user));
  }

  public static Uni<Void> validateEmail(CreateUser createUser) {
    boolean isValid = Utils.isValidEmail(createUser.getEmail());
    if (!isValid) {
      String message = "Invalid email";
      return Uni.createFrom()
          .failure(
              new CustomValidator.CustomValidationException(Map.of("password", message), message));
    } else {
      return Uni.createFrom().voidItem();
    }
  }

  private Function<CreateUser, Uni<? extends User>> verifyUserEmailAndMapToUser(){
    return createUser -> repository.getWithEmail(createUser.getEmail()).onItemOrFailure()
        .transform(Unchecked.function((user, throwable) -> {
          if(Utils.isNull(user)){
            return UserMapper.from(createUser);
          }
          else {
            throw new UserException(UserException.USER_ALREADY_EXISTS, Response.Status.BAD_REQUEST);
          }
        }));
  }

  private Function<Throwable, Throwable> transformToBadRequest(String message, Response.Status status) {
    return throwable -> {
      if (throwable instanceof BaseException) {
        return new UserException(message, status);
      }
      return throwable;
    };
  }

}
