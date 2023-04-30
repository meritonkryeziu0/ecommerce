package app.context;

import app.services.auth.TokenService;
import io.quarkus.arc.Unremovable;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;

import javax.enterprise.context.RequestScoped;

@RequestScoped
@Unremovable
public class UserContext {
  private final String id;
  private final String email;
  private final String role;

  public UserContext(SecurityIdentity securityIdentity){
    DefaultJWTCallerPrincipal defaultJWTCallerPrincipal =
            (DefaultJWTCallerPrincipal) securityIdentity.getPrincipal();
    this.id = defaultJWTCallerPrincipal.getClaim(TokenService.USER_ID);
    this.email = defaultJWTCallerPrincipal.getClaim(TokenService.EMAIL);
    this.role = defaultJWTCallerPrincipal.getClaim(TokenService.ROLE);
  }

  public String getRole(){
    return this.role;
  }
  public String getId(){
    return this.id;
  }
  public String getEmail(){
    return this.id;
  }
}