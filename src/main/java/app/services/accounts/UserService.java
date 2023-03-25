package app.services.accounts;

import app.exceptions.BaseException;
import io.smallrye.mutiny.Uni;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository repository;


    public Uni<User> getById(String id) {
        return repository.getById(id)
                .onItem().ifNull().failWith(new BaseException("User not found", 404));
    }

    public Uni<User> getWithEmail(String email){
        return repository.getWithEmail(email).onItem().ifNull().failWith(new BaseException("User not found", 404));
    }

}
