package app.services.accounts;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoUtils;
import app.services.accounts.exceptions.UserException;
import app.services.accounts.models.CreateUser;
import app.services.accounts.models.UpdateUser;
import app.services.accounts.models.User;
import app.services.auth.models.State;
import app.shared.SuccessResponse;
import app.utils.PasswordUtils;
import app.utils.Utils;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.function.Function;

@ApplicationScoped
public class UserService {
  @Inject
  CustomValidator validator;
  @Inject
  UserRepository repository;

  public Uni<PaginatedResponse<User>> getList(PaginationWrapper wrapper) {
    return MongoUtils.getPaginatedItems(repository.getCollection(), wrapper);
  }

  public Uni<User> getById(String id) {
    return repository.getById(id)
        .onFailure().transform(transformToBadRequest(UserException.USER_NOT_FOUND, Response.Status.BAD_REQUEST));
  }

  public Uni<User> getWithEmail(String email){
    return repository.getWithEmail(email).onFailure().transform(transformToBadRequest(UserException.USER_NOT_FOUND, Response.Status.BAD_REQUEST));
  }

  public Uni<User> add(CreateUser createUser) {
    return PasswordUtils.validatePassword(createUser.getPassword())
        .replaceWith(validator.validate(createUser))
        .flatMap(verifyUserEmailAndMapToUser())
        .flatMap(user -> repository.add(user));
  }

  public Uni<User> update(String id, UpdateUser updateUser) {
    return validator.validate(updateUser).replaceWith(this.getById(id))
        .map(UserMapper.from(updateUser))
        .flatMap(user -> repository.update(id, user));
  }

  public Uni<User> updateState(String id, State state) {
    return repository.updateState(id, state);
  }

  public Uni<SuccessResponse> delete(String id) {
    return repository.delete(id).replaceWith(SuccessResponse.toSuccessResponse());
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
