package app.services.seller;

import app.services.accounts.models.UserReference;
import app.services.product.models.ProductReference;
import app.services.seller.models.SellerProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerProductMapper {
  SellerProductMapper INSTANCE = Mappers.getMapper(SellerProductMapper.class);

  @Mapping(target = "id", ignore = true)
  SellerProduct from(UserReference userReference, ProductReference productReference);

}