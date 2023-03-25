package app.services.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginModel {
    String email;
    String password;
}
