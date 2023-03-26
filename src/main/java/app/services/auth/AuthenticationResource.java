package app.services.auth;

import app.services.accounts.User;
import app.services.accounts.UserService;
import app.services.accounts.models.CreateUser;
import app.utils.PasswordUtils;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationResource {
    @Inject
    AuthenticationService authenticationService;

    @Inject
    UserService userService;
    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/password/{pass}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> password(@PathParam("pass") String password){
        return Uni.createFrom().item("")
                .map(str -> {
                        String salt = PasswordUtils.getSalt();
                        System.out.println(salt);
                        System.out.println(password);
                        String hash = PasswordUtils.hashPassword(password, salt);
                        return String.format("salt : %s \npassword : %s \nhash : %s" , salt, password, hash);
                });
    }

    @POST
    @Path("/sign-in")
    public Uni<User> createUser(CreateUser createUser) {
        return authenticationService.add(createUser);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<String> login(UserLoginModel userLoginModel){
        return authenticationService.authenticate(userLoginModel.getEmail(), userLoginModel.getPassword());
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> logout(){
        return Uni.createFrom().item("Logged out");
    }
}
