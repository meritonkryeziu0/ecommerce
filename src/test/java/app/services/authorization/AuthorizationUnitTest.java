package app.services.authorization;

import app.services.authorization.ability.Ability;
import app.services.roles.RolesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

import static app.utils.Utils.notBlank;

@QuarkusTest
public class AuthorizationUnitTest {
  static String abilityObject = "{\"id\":\"User:ADD\",\"module\":\"User\",\"action\":\"ADD\"},";
  static ObjectMapper mapper = new ObjectMapper();

  @Inject
  RolesService rolesService;

  @Test
  public void authorizationTest() throws JsonProcessingException {
    Ability ability = mapper.readValue(abilityObject, Ability.class);
    UniAssertSubscriber<Object> tester = isAuthorized("Admin", ability)
        .invoke(result -> Assertions.assertTrue(result.isAuthorized()))
        .subscribe().withSubscriber(UniAssertSubscriber.create());
    tester.assertCompleted();
  }

  public Uni<AuthorizedResult> isAuthorized(String role, Ability ability){
    AuthorizedResult authorizedResult = new AuthorizedResult(false);
    if(notBlank(role)){
      //MOCK
      List<Ability> abilities = List.of(new Ability("User:ADD", "User", "ADD"));
      Optional<Ability> matchedAbility = abilities.stream()
          .filter(roleAbility -> roleAbility.getId().equals(ability.constructId()))
          .findFirst();
      if(matchedAbility.isEmpty()){
        return Uni.createFrom().item(authorizedResult);
      }
      authorizedResult.setAuthorized(true);
      return Uni.createFrom().item(authorizedResult);
    }
    return Uni.createFrom().failure(
        new AuthorizationException(AuthorizationException.USERCONTEXT_ROLE_NOT_SET, Response.Status.BAD_REQUEST));
  }

}
