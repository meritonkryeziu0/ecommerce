package app.services.auth;

import io.smallrye.common.constraint.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class UserLoginModel {
    @NotNull
    @Email
    String email;
    @NotNull
    String password;
}
