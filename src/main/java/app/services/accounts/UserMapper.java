package app.services.accounts;

import app.services.accounts.models.CreateUser;
import app.services.accounts.models.UpdateUser;
import app.services.accounts.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.function.Function;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
  @Mapping(target = "roles", expression = "java(List.of(app.services.accounts.models.Roles.Everyone))")
  @Mapping(target = "salt", expression = "java(app.utils.PasswordUtils.getSalt())")
  @Mapping(target = "hashedPassword", expression = "java(app.utils.PasswordUtils.hashPassword(createUser.getPassword(), app.utils.PasswordUtils.getSalt()))")
  User from(CreateUser createUser);

  static Function<User, User> from(UpdateUser updateUser) {
    return user -> {
      user.setPhoneNumber(updateUser.getPhoneNumber());
      user.setBillingAddress(updateUser.getBillingAddress());
      return user;
    };
  }
}