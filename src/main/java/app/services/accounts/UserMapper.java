package app.services.accounts;

import app.services.accounts.models.CreateUser;
import app.services.accounts.models.Roles;
import app.services.accounts.models.UpdateUser;
import app.services.accounts.models.User;
import app.utils.PasswordUtils;

import java.util.List;
import java.util.function.Function;

public class UserMapper {
  public static User from(CreateUser createUser) {
    String salt = PasswordUtils.getSalt();
    String hashedPassword = PasswordUtils.hashPassword(createUser.getPassword(), salt);
    User user = new User();
    user.setRoles(List.of(Roles.Everyone)); //Initial role is Everyone
    user.setFirstName(createUser.getFirstName());
    user.setLastName(createUser.getLastName());
    user.setEmail(createUser.getEmail());
    user.setHashedPassword(hashedPassword);
    user.setSalt(salt);
    user.setPhoneNumber(createUser.getPhoneNumber());
    user.setShippingAddresses(createUser.getShippingAddresses());
    user.setBillingAddress(createUser.getBillingAddress());
    return user;
  }

  public static Function<User, User> from(UpdateUser updateUser) {
    return user -> {
      user.setPhoneNumber(updateUser.getPhoneNumber());
      user.setBillingAddress(updateUser.getBillingAddress());
      return user;
    };
  }
}