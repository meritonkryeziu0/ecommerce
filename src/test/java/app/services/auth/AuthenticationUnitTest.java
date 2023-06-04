package app.services.auth;

import app.services.accounts.models.User;
import app.utils.PasswordUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class AuthenticationUnitTest {
  @Inject
  TokenService tokenService;

  static String userObject = "{\n" +
      "  \"id\": \"6420294456146e33436917c2\",\n" +
      "  \"billingAddress\": {\n" +
      "    \"city\": \"Suhareke\",\n" +
      "    \"street\": \"Rr.Skenderbeu\",\n" +
      "    \"zip\": \"2300\"\n" +
      "  },\n" +
      "  \"email\": \"meriton@gmail.com\",\n" +
      "  \"firstName\": \"Meriton\",\n" +
      "  \"hashedPassword\": \"5507E66FB305DC32DE6C0163F1867796CBBA976882ABB9DDA301555B4CBEEDC8051BC6D058BB322459AEC6CEAD6ED47EFA195DDDDF6C952F5D5D3064871FBD34\",\n" +
      "  \"lastName\": \"Kryeziu\",\n" +
      "  \"phoneNumber\": \"+38349123456\",\n" +
      "  \"salt\": \"F0C69F2491E4BA4327AF72A7254D0807\",\n" +
      "  \"state\": \"LOGGED_IN\",\n" +
      "  \"role\": \"Admin\"\n" +
      "}";
  static ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testToken() throws JsonProcessingException {
    User userMock = mapper.readValue(userObject, User.class);
    UniAssertSubscriber<TokenAuthenticationRequest> tester = tokenService.generateToken(userMock)
        .map(token -> new TokenAuthenticationRequest(new TokenCredential(token, "bearer")))
        .invoke(Assertions::assertNotNull)
        .subscribe().withSubscriber(UniAssertSubscriber.create());
    tester.assertCompleted();
  }

  @Test
  public void verifySuccessfulLogin(){
    String hashed = "5507E66FB305DC32DE6C0163F1867796CBBA976882ABB9DDA301555B4CBEEDC8051BC6D058BB322459AEC6CEAD6ED47EFA195DDDDF6C952F5D5D3064871FBD34";
    String salt = "F0C69F2491E4BA4327AF72A7254D0807";
    boolean result = PasswordUtils.verifyPassword("thisisasecret", hashed, salt);
    Assertions.assertTrue(result);
  }

}
