package app.services.auth;

import app.services.accounts.models.User;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;

import javax.inject.Singleton;

@Singleton
public class TokenService {
  private final int EXPIRY_TIME = 3600 * 4;

  public static final String USER_ID = "userId";
  public static final String EMAIL = "email";

  public Uni<String> generateToken(User user) {
    String token = Jwt.issuer("app-auth")
        .subject("app-auth")
        .groups(user.getRole())
        .expiresAt(System.currentTimeMillis() / 1000 + EXPIRY_TIME)
        .claim("userId", user.getId())
        .claim("email", user.getEmail())
        .sign();
    return Uni.createFrom().item(token);
  }
}
