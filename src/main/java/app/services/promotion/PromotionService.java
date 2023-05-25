package app.services.promotion;

import app.exceptions.BaseException;
import app.services.product.models.Product;
import app.services.seller.SellerProductMapper;
import app.services.seller.models.SellerProduct;
import app.shared.SuccessResponse;
import app.utils.Utils;
import io.smallrye.mutiny.Uni;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
public class PromotionService {
  private final int FIXED_FEE_PERCENTAGE = 6;

  @Inject
  PromotionRepository repository;


  public Uni<List<Product>> getPromotedProducts() {
    return repository.getPromotedProducts();
  }

  public Uni<List<Product>> getPromotedProducts(String id) {
    return repository.getPromotedProducts(id);
  }

  public Uni<SuccessResponse> promoteProduct(String userId, String productId){
    Document filter = new Document(PromotedProduct.FIELD_USER_REFERENCE_ID, userId)
        .append(PromotedProduct.FIELD_PRODUCT_REFERENCE_ID, productId);
    return SellerProduct.find(filter).firstResult()
        .map(Utils.mapTo(SellerProduct.class))
        .map(sellerProduct ->
            SellerProductMapper.INSTANCE.toPromotedProduct(sellerProduct, FIXED_FEE_PERCENTAGE))
        .call(promotedProduct -> {
          if(Utils.notNull(promotedProduct)){
            return promotedProduct.persist();
          }
          return Uni.createFrom().item(new BaseException("Product doesnt exist", Response.Status.BAD_REQUEST));
        })
        .map(SuccessResponse.success());
  }

  public Uni<SuccessResponse> unPromoteProduct(String userId, String productId) {
    Document filter = new Document(PromotedProduct.FIELD_USER_REFERENCE_ID, userId)
        .append(PromotedProduct.FIELD_PRODUCT_REFERENCE_ID, productId);
    return PromotedProduct.delete(filter)
        .map(SuccessResponse.success());
  }

  public Uni<SuccessResponse> unPromoteProduct(String productId) {
    return PromotedProduct.delete(PromotedProduct.FIELD_PRODUCT_REFERENCE_ID, productId)
        .map(SuccessResponse.success());
  }


}
