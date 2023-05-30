package app.context;

import app.services.auth.TokenService;
import app.services.auth.exceptions.AuthenticationException;
import io.quarkus.arc.Unremovable;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;

import static app.utils.Utils.notNull;

@RequestScoped
@Unremovable
public class UserContext {
  private final String id;
  private final String email;
  private final String role;

  public UserContext(SecurityIdentity securityIdentity){
    if(notNull(securityIdentity)){
      DefaultJWTCallerPrincipal defaultJWTCallerPrincipal =
          (DefaultJWTCallerPrincipal) securityIdentity.getPrincipal();
      this.id = defaultJWTCallerPrincipal.getClaim(TokenService.USER_ID);
      this.email = defaultJWTCallerPrincipal.getClaim(TokenService.EMAIL);
      this.role = defaultJWTCallerPrincipal.getClaim(TokenService.ROLE);
    }else {
      throw new AuthenticationException(AuthenticationException.TOKEN_IS_NULL, Response.Status.BAD_REQUEST);
    }
  }

  public String getRole(){
    return this.role;
  }
  public String getId(){
    return this.id;
  }
  public String getEmail(){
    return this.email;
  }
}