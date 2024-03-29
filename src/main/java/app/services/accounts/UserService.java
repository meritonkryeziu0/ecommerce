package app.services.accounts;

import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoSessionWrapper;
import app.mongodb.MongoUtils;
import app.services.accounts.exceptions.UserException;
import app.services.accounts.models.*;
import app.services.auth.models.State;
import app.services.shoppingcart.ShoppingCartService;
import app.services.shoppingcart.models.CreateShoppingCart;
import app.services.wishlist.WishlistService;
import app.services.wishlist.models.CreateWishlist;
import app.shared.SuccessResponse;
import app.utils.PasswordUtils;
import app.utils.Utils;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        .flatMap(verifyCreateUserEmail())
        .map(validUser -> UserMapper.INSTANCE.from(createUser))
        .flatMap(addUser());
  }

  public Uni<User> add(User user) {
    return Uni.createFrom().item(user)
        .flatMap(verifyUserEmail())
        .flatMap(addUser());
  }

  private Function<User, Uni<? extends User>> addUser(){
    return user -> sessionWrapper.getSession().flatMap(
        session ->
            repository.add(session, user)
                .replaceWith(shoppingCartService.add(session, new CreateShoppingCart(user)))
                .replaceWith(wishlistService.add(session, new CreateWishlist(user)))
                .replaceWith(Uni.createFrom().publisher(session.commitTransaction()))
                .replaceWith(user)
                .eventually(session::close)
    );
  }

  public Uni<User> register(RegisterUser registerUser) {
    return add(UserMapper.INSTANCE.from(registerUser));
  }

  public Uni<User> update(String id, UpdateUser updateUser) {
    return validator.validate(updateUser)
        .replaceWith(this.getById(id))
        .map(UserMapper.from(updateUser))
        .call(MongoUtils::updateEntity);
  }

  public Uni<User> updateRole(String id, UpdateRole updateRole) {
    return validator.validate(updateRole)
        .replaceWith(this.getById(id))
        .map(UserMapper.from(updateRole))
        .call(MongoUtils::updateEntity);
  }

  public Uni<User> updateState(String id, State state) {
    return repository.updateState(id, state);
  }

  public Uni<SuccessResponse> delete(String id) {
    return User.deleteById(id).replaceWith(SuccessResponse.toSuccessResponse());
  }

  private Function<CreateUser, Uni<? extends CreateUser>> verifyCreateUserEmail() {
    return createUser -> User.find(User.FIELD_EMAIL, createUser.getEmail()).firstResult()
        .onItem().ifNotNull().failWith(new UserException(UserException.USER_ALREADY_EXISTS, Response.Status.BAD_REQUEST))
        .map(panacheUser -> createUser);
  }

  private Function<User, Uni<? extends User>> verifyUserEmail() {
    return user -> User.find(User.FIELD_EMAIL, user.getEmail()).firstResult()
        .onItem().ifNotNull()
        .failWith(new UserException(UserException.USER_ALREADY_EXISTS, Response.Status.BAD_REQUEST))
        .map(panacheUser -> user);
  }

  public Uni<User> addShippingAddress(String id, ShippingAddress shippingAddress) {
    return validator.validate(shippingAddress)
        .map(valid -> this.getById(id))
        .onFailure().transform(transformToBadRequest(UserException.USER_NOT_FOUND, Response.Status.BAD_REQUEST))
        .flatMap(user -> repository.addShippingAddress(id, shippingAddress));
  }

  public Uni<User> editShippingAddress(String id, String shippingId, ShippingAddress shippingAddress) {
    return this.getById(id).map(User::getShippingAddresses)
        .map(Unchecked.function(list ->
            list.stream().filter(item -> Objects.equals(item.getId(), shippingId))
                .findFirst().orElseThrow(() -> new UserException(UserException.SHIPPING_ADDRESS_NOT_FOUND, Response.Status.BAD_REQUEST))))
        .flatMap(
            shippingAddressOld -> {
              shippingAddressOld.setCity(shippingAddress.getCity());
              shippingAddressOld.setStreet(shippingAddress.getStreet());
              shippingAddressOld.setZip(shippingAddress.getZip());
              return repository.editShippingAddress(id, shippingId, shippingAddressOld);
            }
        );
  }

  public Uni<User> deleteShippingAddress(String id, ShippingAddress shippingAddress) {
    return repository.deleteShippingAddress(id, shippingAddress)
        .flatMap(user -> reorderShippingAddresses(user.id, user.getShippingAddresses()));
  }

  private Uni<User> reorderShippingAddresses(String userId, List<ShippingAddress> shippingAddresses) {
    AtomicInteger minIndex = new AtomicInteger(1);

    List<ShippingAddress> addresses = shippingAddresses.stream().map(shippingAddress -> {
      shippingAddress.setId(String.valueOf(minIndex.getAndIncrement()));
      return shippingAddress;
    }).collect(Collectors.toList());

    return repository.setUserShippingAddresses(userId, addresses);
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
