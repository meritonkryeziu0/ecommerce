package app.services.product;

import app.services.product.models.CreateProduct;
import app.services.product.models.Product;
import app.services.product.models.ProductReference;
import app.services.product.models.UpdateProduct;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;

import java.time.LocalDateTime;
import java.util.function.Function;

public class ProductMapper {
  public static Product from(CreateProduct createProduct) {
    Product product = new Product();
    product.setName(createProduct.getName());
    product.setDescription(createProduct.getDescription());
    product.setPrice(createProduct.getPrice());
    product.setStockQuantity(createProduct.getStockQuantity());
    product.setType(createProduct.getType());
    product.setDetails(createProduct.getDetails());
    product.setManufacturer(createProduct.getManufacturer());
    product.setPackageDetails(createProduct.getPackageDetails());
    return product;
  }

  public static ProductReference toProductReference(Product product) {
    ProductReference productReference = new ProductReference();
    productReference.set_id(product.getId());
    productReference.setName(product.getName());
    productReference.setPrice(product.getPrice());
    return productReference;
  }

  public static Function<ReactivePanacheMongoEntityBase, Product> mapToProduct(){
    return reactivePanacheMongoEntityBase -> (Product) reactivePanacheMongoEntityBase;
  }

  public static Function<Product, Product> from(UpdateProduct updateProduct) {
    return product -> {
      product.setName(updateProduct.getName());
      product.setDescription(updateProduct.getDescription());
      product.setPrice(updateProduct.getPrice());
      product.setStockQuantity(updateProduct.getStockQuantity());
      product.setDetails(updateProduct.getDetails());
      product.setManufacturer(updateProduct.getManufacturer());
      return product;
    };
  }
}
