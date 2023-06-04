package app.services.common;


import app.common.CustomValidator;
import app.common.ListWrapper;
import app.services.accounts.models.CreateUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

@QuarkusTest
public class CustomValidatorUnitTest {
  @Inject
  CustomValidator validator;
  static String createUserObject = "{\n" +
      "  \"billingAddress\": {\n" +
      "    \"city\": \"Prishtine\",\n" +
      "    \"street\": \"Rr.Rexhep Mala\",\n" +
      "    \"zip\": \"10000\"\n" +
      "  },\n" +
      "  \"email\": \"meriton2@gmail.com\",\n" +
      "  \"firstName\": \"Meriton\",\n" +
      "  \"lastName\": \"Kryeziu\",\n" +
      "  \"password\": \"thisisasecret\",\n" +
      "  \"phoneNumber\": \"+38349123456\",\n" +
      "  \"role\": \"Admin\"\n" +
      "}";
  static String createUserObject2 = "{\n" +
      "  \"billingAddress\": {\n" +
      "    \"city\": \"Suhareke\",\n" +
      "    \"street\": \"Rr.Skenderbeu\",\n" +
      "    \"zip\": \"2300\"\n" +
      "  },\n" +
      "  \"email\": \"meriton1@gmail.com\",\n" +
      "  \"firstName\": \"Meriton\",\n" +
      "  \"lastName\": \"Kryeziu\",\n" +
      "  \"password\": \"thisisasecret\",\n" +
      "  \"phoneNumber\": \"+38349123456\",\n" +
      "  \"role\": \"User\"\n" +
      "}";
  static ObjectMapper mapper = new ObjectMapper();

  @Test
  public void objectValidatorTest() throws JsonProcessingException {
    CreateUser createUser = mapper.readValue(createUserObject, CreateUser.class);
    UniAssertSubscriber<Object> tester = validator.validate(createUser)
        .subscribe().withSubscriber(UniAssertSubscriber.create());
    tester.assertCompleted();
  }

  @Test
  public void listValidatorTest() throws JsonProcessingException {
    List<CreateUser> createUsers = List.of(mapper.readValue(createUserObject, CreateUser.class),
        mapper.readValue(createUserObject2, CreateUser.class));
    UniAssertSubscriber<ListWrapper<CreateUser>> tester = validator.validateList(createUsers)
        .subscribe().withSubscriber(UniAssertSubscriber.create());
    tester.assertCompleted();
  }

}
