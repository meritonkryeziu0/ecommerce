package app.services.auth;

import app.services.accounts.User;
import app.services.accounts.UserService;
import app.services.auth.exceptions.AuthenticationException;
import app.utils.PasswordUtils;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AuthenticationService {
    @Inject
    UserService userService;
    @Inject
    GenerateToken generateToken;

    public Uni<String> authenticate(String email, String password){
        return userService.getWithEmail(email)
                .flatMap(user -> this.verifySuccessfulLogin(password, user))
                .flatMap(loggedInUser -> generateToken.generateToken(loggedInUser));
    }

    private Uni<User> verifySuccessfulLogin(String password, User user){
        boolean passwordResult = PasswordUtils.verifyPassword(password, user);
        if(passwordResult){
            return Uni.createFrom().item(user);
        }
        return Uni.createFrom()
                .emitter(emitter -> {
                    AuthenticationException authenticationException =
                            new AuthenticationException.InvalidCredentialsException("Invalid credentials");
                    emitter.fail(authenticationException);
                });
    }
}
