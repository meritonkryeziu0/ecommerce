package app.services.accounts;

import app.services.accounts.exception.UserException;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository repository;

    public Uni<User> getById(String id) {
        return repository.getById(id)
                .onItem().ifNull().failWith(new UserException(UserException.USER_NOT_FOUND, Response.Status.NOT_FOUND));
    }

    public Uni<User> getWithEmail(String email){
        return repository.getWithEmail(email).onItem().ifNull().failWith(new UserException(UserException.USER_NOT_FOUND, Response.Status.NOT_FOUND));
    }

}
