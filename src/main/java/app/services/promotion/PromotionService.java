package app.services.promotion;

import app.services.product.exceptions.ProductException;
import app.services.product.models.Product;
import app.services.seller.SellerProductMapper;
import app.services.seller.models.SellerProduct;
import app.shared.SuccessResponse;
import app.utils.Utils;
import com.mongodb.reactivestreams.client.ClientSession;
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
    return PromotedProduct.find(filter).firstResult().onItem()
        .ifNotNull()
        .failWith(new PromotedProductException(PromotedProductException.PRODUCT_ALREADY_PROMOTED, Response.Status.BAD_REQUEST))
            .replaceWith(SellerProduct.find(filter).firstResult())
        .flatMap(Utils.mapToUni(SellerProduct.class))
        .map(sellerProduct ->
            SellerProductMapper.INSTANCE.toPromotedProduct(sellerProduct, FIXED_FEE_PERCENTAGE))
        .call(promotedProduct -> {
          if(Utils.notNull(promotedProduct)){
            return promotedProduct.persist();
          }
          return Uni.createFrom().failure(new PromotedProductException(ProductException.PRODUCT_NOT_FOUND, Response.Status.BAD_REQUEST));
        })
        .flatMap(SuccessResponse.successAsUni());
  }

  public Uni<SuccessResponse> unPromoteProduct(String userId, String productId) {
    Document filter = new Document(PromotedProduct.FIELD_USER_REFERENCE_ID, userId)
        .append(PromotedProduct.FIELD_PRODUCT_REFERENCE_ID, productId);
    return PromotedProduct.delete(filter)
        .flatMap(SuccessResponse.successAsUni());
  }

  public Uni<SuccessResponse> unPromoteProduct(String productId) {
    return PromotedProduct.delete(PromotedProduct.FIELD_PRODUCT_REFERENCE_ID, productId)
        .map(SuccessResponse.success());
  }

  public Uni<Void> delete(ClientSession session, String id) {
    return repository.delete(session, id);
  }
}
