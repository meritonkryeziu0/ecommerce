package app.services.context;

import app.services.auth.TokenService;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import lombok.Getter;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import java.util.Set;

@Getter
@RequestScoped
public class UserContext {
  private final String id;
  private final String userId;
  private final String email;
  private final String role;

  public UserContext(SecurityIdentity securityIdentity){
    DefaultJWTCallerPrincipal defaultJWTCallerPrincipal =
        (DefaultJWTCallerPrincipal) securityIdentity.getPrincipal();
    this.id = defaultJWTCallerPrincipal.getSubject();
    this.userId = defaultJWTCallerPrincipal.getClaim(TokenService.USER_ID);
    this.email = defaultJWTCallerPrincipal.getClaim(TokenService.EMAIL);
    Set<String> roles = securityIdentity.getRoles();
    this.role = roles.size() > 0 ? roles.iterator().next() : null;
  }
}
