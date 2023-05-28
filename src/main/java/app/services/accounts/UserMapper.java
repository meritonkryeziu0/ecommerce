package app.services.accounts;

import app.services.accounts.models.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.function.Function;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mapping(target = "salt", expression = "java(app.utils.PasswordUtils.getSalt())")
  @Mapping(target = "hashedPassword", expression = "java(app.utils.PasswordUtils.hashPassword(createUser.getPassword(), app.utils.PasswordUtils.getSalt()))")
  User from(CreateUser createUser);

  @Mapping(target = "role", constant = "User")
  @Mapping(target = "salt", expression = "java(app.utils.PasswordUtils.getSalt())")
  @Mapping(target = "hashedPassword", expression = "java(app.utils.PasswordUtils.hashPassword(registerUser.getPassword(), app.utils.PasswordUtils.getSalt()))")
  User from(RegisterUser registerUser);

  UserReference toUserReference(User user);

  static Function<User, User> from(UpdateUser updateUser) {
    return user -> {
      user.setPhoneNumber(updateUser.getPhoneNumber());
      user.setBillingAddress(updateUser.getBillingAddress());
      return user;
    };
  }

  static Function<User, User> from(UpdateRole updateRole) {
    return user -> {
      user.setRole(updateRole.getRole());
      return user;
    };
  }
}