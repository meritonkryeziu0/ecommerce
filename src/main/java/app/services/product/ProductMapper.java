package app.services.product;

import app.services.product.models.CreateProduct;
import app.services.product.models.Product;
import app.services.product.models.ProductReference;
import app.services.product.models.UpdateProduct;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.function.Function;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
  ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

  Product from(CreateProduct createProduct);

  ProductReference toProductReference(Product product);

  static Function<Product, Product> from(UpdateProduct updateProduct) {
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
