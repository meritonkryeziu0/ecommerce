package app.services.wishlist;

import app.services.wishlist.models.CreateWishlist;
import app.services.wishlist.models.Wishlist;

import java.util.ArrayList;

public class WishlistMapper {
  public static Wishlist from(CreateWishlist createWishlist) {
    Wishlist wishlist = new Wishlist();
    wishlist.setUserReference(createWishlist.getUserReference());
    wishlist.setProducts(new ArrayList<>());
    return wishlist;
  }
}
