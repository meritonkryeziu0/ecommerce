package app.services.auth;

import app.services.accounts.UserService;
import app.services.accounts.models.CreateUser;
import app.services.accounts.models.User;
import app.services.auth.exceptions.AuthenticationException;
import app.services.auth.models.AuthResponse;
import app.services.auth.models.State;
import app.utils.PasswordUtils;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.graalvm.collections.Pair;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static app.utils.Utils.isBlank;

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

  public Uni<User> add(CreateUser createUser) {
    return userService.add(createUser);
  }

  public Uni<AuthResponse> userLogOut(JsonWebToken jwt) {
    if (isBlank(jwt.getRawToken())) {
      return Uni.createFrom().failure(
          new AuthenticationException(AuthenticationException.TOKEN_IS_NULL, Response.Status.BAD_REQUEST)
      );
    }
    String email = jwt.getClaim("email");
    State state = State.LOGGED_OUT;
    return userService.getWithEmail(email)
        .flatMap(user -> userService.updateState(user.getId(), state))
        .map(user -> new AuthResponse(state, user.getId(), null));
  }

}
