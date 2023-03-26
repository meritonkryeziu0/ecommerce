package app.services.auth;

import app.services.accounts.User;
import app.services.accounts.UserService;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GenerateToken {

  private final int EXPIRY_TIME = 3600 * 4;
  @Inject
  UserService userService;

  public Uni<String> generateToken(User user) {
    String token = Jwt.issuer("app-auth")
        .subject("app-auth")
        .groups(user.getRole())
        .expiresAt(System.currentTimeMillis() + EXPIRY_TIME)
        .claim("userId", user.getId())
        .claim("email", user.getEmail())
        .sign();
    return Uni.createFrom().item(token);
  }
}
