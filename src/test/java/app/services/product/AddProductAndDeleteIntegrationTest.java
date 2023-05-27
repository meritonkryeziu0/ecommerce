package app.services.product;

import app.services.product.models.CreateProduct;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class AddProductAndDeleteIntegrationTest {
  String newProduct = "{\n" +
      "  \"name\": \"TEST\",\n" +
      "  \"description\": \"testproduct\",\n" +
      "  \"price\": 142,\n" +
      "  \"stockQuantity\": 10,\n" +
      "  \"imageUrl\": \"https://alaskabeacon.com/wp-content/uploads/2022/07/main_image_deep_field_smacs0723-1280.jpeg\",\n" +
      "  \"manufacturer\": {\n" +
      "  \"id\": \"6439deba2e64982ffad87e45\",\n" +
      "  \"address\": {\n" +
      "    \"city\": \"p\",\n" +
      "    \"street\": \">\",\n" +
      "    \"zip\": \"'\"\n" +
      "  },\n" +
      "  \"name\": \"ernatest\"\n" +
      "},\n" +
      "  \"category\":{\n" +
      "  \"id\": \"6437eb9a4a72d04fab5ff3c3\",\n" +
      "  \"name\": \"TestCategory\"\n" +
      "},\n" +
      "  \"details\": {\n" +
      "      \"test1\" : \"test\"\n" +
      "  },\n" +
      "  \"packageDetails\": {\n" +
      "    \"weight\": 1,\n" +
      "    \"height\": 1,\n" +
      "    \"width\": 2,\n" +
      "    \"depth\": 2\n" +
      "  }\n" +
      "}";

  static ObjectMapper mapper = new ObjectMapper();
  @Inject ProductService productService;

  @Test
  public void addProductIntegrationTest() throws JsonProcessingException {
    CreateProduct product = mapper.readValue(newProduct, CreateProduct.class);
    UniAssertSubscriber<Object> tester = productService.add(product)
        .invoke(addedProduct -> Assertions.assertEquals(product.getName(), addedProduct.getName()))
        .subscribe().withSubscriber(UniAssertSubscriber.create());
    tester.awaitItem();
  }

  @Test
  public void deleteAddedProduct() {
    UniAssertSubscriber<Object> tester = productService.delete("TEST", "testproduct")
        .subscribe().withSubscriber(UniAssertSubscriber.create());
    tester.awaitItem();
  }

}
