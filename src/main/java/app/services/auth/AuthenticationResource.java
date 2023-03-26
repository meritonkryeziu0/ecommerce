package app.services.auth;

import app.services.accounts.UserService;
import app.utils.PasswordUtils;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.security.NoSuchAlgorithmException;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationResource {
    @Inject
    AuthenticationService authenticationService;

    @Inject
    UserService userService;

    @POST
    @Path("/password/{pass}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> password(@PathParam("pass") String password){
        return Uni.createFrom().item("")
                .map(Unchecked.function(str -> {
                    try {
                        String salt = PasswordUtils.getSalt();
                        System.out.println(salt);
                        System.out.println(password);
                        String hash = PasswordUtils.hashPassword(password, salt);
                        return String.format("salt : %s \npassword : %s \nhash : %s" , salt, password, hash);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }));
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

    //TOOD :Add user from CreateUser:
    /*@POST
    @Path("/sign-in")
    public Uni<String> createUser(CreateUser createUser) {
    }*/
}
