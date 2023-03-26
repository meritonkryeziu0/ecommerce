package app.services.accounts;

import app.services.accounts.models.CreateUser;
import app.utils.PasswordUtils;

public class UserMapper {
    public static User from(CreateUser createUser){
        String salt = PasswordUtils.getSalt();
        String hashedPassword = PasswordUtils.hashPassword(createUser.getPassword(), salt);
        User user = new User();
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
}