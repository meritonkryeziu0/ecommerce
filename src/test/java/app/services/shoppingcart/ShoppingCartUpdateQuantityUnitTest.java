package app.services.shoppingcart;

import app.services.product.models.ProductReference;
import app.services.shoppingcart.models.ShoppingCart;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class ShoppingCartUpdateQuantityUnitTest {

  static String shoppingCart = "{\n" +
      "  \"id\": \"645d51ad4db93e0292c2be20\",\n" +
      "  \"products\": [\n" +
      "    {\n" +
      "      \"id\": \"4bd3f54d-0554-4e5b-b2dc-4a87b3faeac3\",\n" +
      "      \"name\": \"Licensed Frozen Salad\",\n" +
      "      \"price\": 99.27,\n" +
      "      \"quantity\": 1\n" +
      "    }\n" +
      "  ],\n" +
      "  \"total\": 99.27,\n" +
      "  \"userReference\": {\n" +
      "    \"id\": \"645d51ad4db93e0292c2be1f\",\n" +
      "    \"email\": \"meritonuser@gmail.com\",\n" +
      "    \"firstName\": \"Meriton\",\n" +
      "    \"lastName\": \"Kryeziu\"\n" +
      "  }\n" +
      "}";

  static String reference = "{\n" +
      "\"id\": \"4bd3f54d-0554-4e5b-b2dc-4a87b3faeac3\",\n" +
      "\"name\": \"Licensed Frozen Salad\",\n" +
      "\"price\": 99.27,\n" +
      "\"quantity\": 2\n" +
      "}";

  static ObjectMapper mapper = new ObjectMapper();

  @Inject ShoppingCartService shoppingCartService;

  @Test
  public void updateShoppingCartTest() throws JsonProcessingException {
    ShoppingCart cart = mapper.readValue(shoppingCart, ShoppingCart.class);
    ProductReference productReference = mapper.readValue(reference, ProductReference.class);
    ShoppingCart updatedCart = shoppingCartService.updateShoppingCart(cart, productReference);
    Assertions.assertEquals(updatedCart.getTotal(), 198.54);
  }

}
