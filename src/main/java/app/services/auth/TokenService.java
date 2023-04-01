package app.services.auth;

import app.services.accounts.models.User;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;

import javax.inject.Singleton;

@Singleton
public class TokenService {
    private final int EXPIRY_TIME = 3600*4;

    public Uni<String> generateToken(User user){
        String token = Jwt.issuer("app-auth")
            .subject("app-auth")
            .groups(user.fetchRolesString())
            .expiresAt(System.currentTimeMillis() / 1000 + EXPIRY_TIME)
            .claim("userId", user.getId())
            .claim("email", user.getEmail())
            .sign();
        return Uni.createFrom().item(token);
    }
}
