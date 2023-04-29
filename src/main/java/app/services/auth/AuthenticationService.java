package app.services.auth;

import app.context.UserContext;
import app.services.accounts.UserService;
import app.services.accounts.models.RegisterUser;
import app.services.accounts.models.User;
import app.services.auth.exceptions.AuthenticationException;
import app.services.auth.models.AuthResponse;
import app.services.auth.models.State;
import app.utils.PasswordUtils;
import io.smallrye.mutiny.Uni;
import org.graalvm.collections.Pair;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AuthenticationService {
  @Inject
  UserService userService;
  @Inject
  TokenService tokenService;


  public Uni<AuthResponse> authenticate(String email, String password) {
    return userService.getWithEmail(email)
        .flatMap(user -> this.verifySuccessfulLogin(password, user))
        .flatMap(user -> userService.updateState(user.getId(), State.LOGGED_IN))
        .flatMap(loggedInUser -> tokenService.generateToken(loggedInUser)
            .map(token -> Pair.create(loggedInUser, token)))
        .map(userTokenPair -> new AuthResponse(State.LOGGED_IN, userTokenPair.getLeft().getEmail(), userTokenPair.getRight()));
  }

  private Uni<User> verifySuccessfulLogin(String password, User user) {
    boolean passwordResult = PasswordUtils.verifyPassword(password, user);
    if (passwordResult) {
      return Uni.createFrom().item(user);
    }
    return Uni.createFrom()
        .emitter(emitter -> {
          AuthenticationException authenticationException =
              new AuthenticationException.InvalidCredentialsException("Invalid credentials");
          emitter.fail(authenticationException);
        });
  }

  public Uni<User> register(RegisterUser registerUser) {
    return userService.register(registerUser);
  }

  public Uni<AuthResponse> userLogOut(UserContext userContext) {
    State state = State.LOGGED_OUT;
    return userService.getWithEmail(userContext.getEmail())
        .flatMap(user -> userService.updateState(user.getId(), state))
        .map(user -> new AuthResponse(state, user.getId(), null));
  }

}
