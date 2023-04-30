package app.services.auth;

import app.services.accounts.models.User;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;

import javax.inject.Singleton;

@Singleton
public class TokenService {
  private final int EXPIRY_TIME = 3600 * 4;

  //OIDC
  public static final String ISSUER = "app-auth";
  public static final String SUBJECT = "app-auth";
  public static final String USER_ID = "userId";
  public static final String EMAIL = "email";
  public static final String ROLE = "role";

  public Uni<String> generateToken(User user) {
    String token = Jwt.issuer(ISSUER)
        .subject(SUBJECT)
        .expiresAt(System.currentTimeMillis() / 1000 + EXPIRY_TIME)
        .claim(USER_ID, user.getId())
        .claim(EMAIL, user.getEmail())
        .claim(ROLE, user.getRole())
        .sign();
    return Uni.createFrom().item(token);
  }
}
