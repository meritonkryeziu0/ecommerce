package app.services.auth;

import app.context.UserContext;
import app.services.accounts.models.RegisterUser;
import app.services.accounts.models.User;
import app.services.auth.models.AuthResponse;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationResource {
  @Inject
  AuthenticationService authenticationService;
  @Inject
  UserContext userContext;

  @POST
  @Path("/register")
  @PermitAll
  public Uni<User> register(RegisterUser registerUser) {
    return authenticationService.register(registerUser);
  }

  @POST
  @Path("/login")
  @PermitAll
  public Uni<AuthResponse> login(@Valid UserLoginModel userLoginModel) {
    return authenticationService.authenticate(userLoginModel.getEmail(), userLoginModel.getPassword());
  }

  @POST
  @Path("/logout")
  public Uni<AuthResponse> logout() {
    return authenticationService.userLogOut(userContext).map(token -> new AuthResponse());
  }
}
