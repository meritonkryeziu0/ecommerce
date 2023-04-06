package app.services.auth;

import app.services.accounts.models.CreateUser;
import app.services.accounts.models.User;
import app.services.auth.models.AuthResponse;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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
    JsonWebToken jwt;

    @POST
    @Path("/register")
    public Uni<User> createUser(CreateUser createUser) {
        return authenticationService.add(createUser);
    }

    @POST
    @Path("/login")
    @PermitAll
    public Uni<AuthResponse> login(@Valid UserLoginModel userLoginModel){
        return authenticationService.authenticate(userLoginModel.getEmail(), userLoginModel.getPassword());
    }

    @POST
    @Path("/logout")
    @RolesAllowed({"Everyone"})
    public Uni<AuthResponse> logout(){
        return authenticationService.userLogOut(jwt).map(token -> new AuthResponse());
    }
}
