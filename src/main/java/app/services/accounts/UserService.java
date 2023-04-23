package app.services.accounts;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoCollectionWrapper;
import app.mongodb.MongoCollections;
import app.mongodb.MongoSessionWrapper;
import app.mongodb.MongoUtils;
import app.services.accounts.exceptions.UserException;
import app.services.accounts.models.CreateUser;
import app.services.accounts.models.UpdateUser;
import app.services.accounts.models.User;
import app.services.auth.models.State;
import app.services.shoppingcart.ShoppingCartService;
import app.services.shoppingcart.models.CreateShoppingCart;
import app.services.wishlist.WishlistService;
import app.services.wishlist.models.CreateWishlist;
import app.shared.SuccessResponse;
import app.utils.PasswordUtils;
import app.utils.Utils;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
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
  @Inject
  ShoppingCartService shoppingCartService;

  @Inject
  WishlistService wishlistService;

  @Inject
  MongoSessionWrapper sessionWrapper;


  public Uni<PaginatedResponse<User>> getList(PaginationWrapper wrapper) {
    return MongoUtils.getPaginatedItems(repository.getCollection(), wrapper);
  }

  public Uni<User> getById(String id) {
    return User.findById(id)
        .onItem().ifNull().failWith(new UserException(UserException.USER_NOT_FOUND, Response.Status.BAD_REQUEST))
        .map(Utils.mapTo(User.class));
  }

  public Uni<User> getWithEmail(String email) {
    return User.find(User.FIELD_EMAIL, email).firstResult()
        .onItem().ifNull().failWith(new UserException(UserException.USER_NOT_FOUND, Response.Status.BAD_REQUEST))
        .map(Utils.mapTo(User.class));
  }

  public Uni<User> add(CreateUser createUser) {
    return PasswordUtils.validatePassword(createUser.getPassword())
        .replaceWith(validator.validate(createUser))
        .flatMap(verifyUserEmailAndMapToUser())
        .flatMap(user -> sessionWrapper.getSession().flatMap(
            session ->
                repository.add(session, user)
                    .replaceWith(shoppingCartService.add(session, new CreateShoppingCart(user)))
                    .replaceWith(wishlistService.add(session, new CreateWishlist(user)))
                    .replaceWith(Uni.createFrom().publisher(session.commitTransaction()))
                    .replaceWith(user)
                    .eventually(session::close)
        ));
  }

  public Uni<User> update(String id, UpdateUser updateUser) {
    return validator.validate(updateUser)
        .replaceWith(this.getById(id))
        .map(UserMapper.from(updateUser))
        .call(MongoUtils::updateEntity);
  }

  public Uni<User> updateState(String id, State state) {
    return repository.updateState(id, state);
  }

  public Uni<SuccessResponse> delete(String id) {
    return User.deleteById(id).replaceWith(SuccessResponse.toSuccessResponse());
  }

  private Function<CreateUser, Uni<? extends User>> verifyUserEmailAndMapToUser() {
    return createUser -> User.find(User.FIELD_EMAIL, createUser.getEmail()).firstResult().onItemOrFailure()
        .transform(Unchecked.function((user, throwable) -> {
          if (Utils.isNull(user)) {
            return UserMapper.from(createUser);
          } else {
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
