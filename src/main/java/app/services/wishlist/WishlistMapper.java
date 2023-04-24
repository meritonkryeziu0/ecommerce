package app.services.wishlist;

import app.services.wishlist.models.CreateWishlist;
import app.services.wishlist.models.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WishlistMapper {
  WishlistMapper INSTANCE = Mappers.getMapper(WishlistMapper.class);

  @Mapping(target = "products", expression = "java(new java.util.ArrayList<>())")
  Wishlist from(CreateWishlist createWishlist);
}