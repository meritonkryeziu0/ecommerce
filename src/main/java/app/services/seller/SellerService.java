package app.services.seller;

import app.common.CustomValidator;
import app.context.UserContext;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoSessionWrapper;
import app.services.accounts.UserMapper;
import app.services.accounts.UserService;
import app.services.accounts.models.CreateUser;
import app.services.accounts.models.UpdateUser;
import app.services.accounts.models.User;
import app.services.accounts.models.UserReference;
import app.services.product.ProductMapper;
import app.services.product.ProductService;
import app.services.product.models.CreateProduct;
import app.services.product.models.Product;
import app.services.product.models.ProductReference;
import app.services.product.models.UpdateProduct;
import app.services.seller.models.SellerProduct;
import app.shared.SuccessResponse;
import app.utils.Utils;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.mutiny.Uni;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped

public class SellerService {
  @Inject
  UserService userService;
  @Inject
  ProductService productService;
  @Inject
  MongoSessionWrapper sessionWrapper;
  @Inject
  SellerProductRepository sellerProductRepository;
  @Inject
  CustomValidator validator;

  public Uni<PaginatedResponse<User>> getList(PaginationWrapper wrapper) {
    return userService.getList(wrapper);
  }

  public Uni<List<Product>> getSellerProducts(String id) {
    return sellerProductRepository.getSellersProducts(id);
  }

  public Uni<User> addSeller(CreateUser createUser) {
    return userService.add(createUser);
  }

  public Uni<User> update(String id, UpdateUser updateUser) {
    return userService.update(id, updateUser);
  }

  public Uni<SuccessResponse> delete(String id) {
    return userService.delete(id);
  }

  public Uni<SuccessResponse> addProduct(UserContext userContext, CreateProduct createProduct){
    return validator.validate(createProduct)
        .flatMap(ignore -> userService.getById(userContext.getId()))
        .map(UserMapper.INSTANCE::toUserReference)
        .flatMap(userReference -> sessionWrapper.getSession()
            .flatMap(session -> productService.add(session, createProduct)//add product
                .call(product -> {
                  ProductReference productReference = ProductMapper.INSTANCE.toProductReference(product);
                  return addProduct(session, userReference, productReference);
                })//add seller-product
                .replaceWith(Uni.createFrom().publisher(session.commitTransaction()))
                .eventually(session::close)
            ))
        .map(aVoid -> SuccessResponse.toSuccessResponse());
  }

  public Uni<SellerProduct> addProduct(ClientSession session,
      UserReference userReference,
      ProductReference productReference) {
    return validator.validate(userReference)
        .map(ignore -> SellerProductMapper.INSTANCE.from(userReference, productReference))
        .call(sellerProduct ->  sellerProductRepository.add(session, sellerProduct));
  }

  public Uni<SuccessResponse> updateProduct(UserContext userContext,String productId, UpdateProduct updateProduct){
    Document filter = new Document(SellerProduct.FIELD_USER_REFERENCE_ID, userContext.getId())
        .append(SellerProduct.FIELD_PRODUCT_REFERENCE_ID, productId);
    return SellerProduct.find(filter)
        .firstResult()
        .map(Utils.mapTo(SellerProduct.class))
        .flatMap(sellerProduct -> sessionWrapper.getSession()
            .flatMap(session -> productService.update(session, sellerProduct.getProductReferenceId(), updateProduct)//update product
                .call(product -> {
                  ProductReference productReference = ProductMapper.INSTANCE.toProductReference(product);
                  return updateProduct(session, sellerProduct.getUserReference(), productReference);
                })//update seller-product
                .replaceWith(Uni.createFrom().publisher(session.commitTransaction()))
                .eventually(session::close)
            ))
        .map(aVoid -> SuccessResponse.toSuccessResponse());
  }

  public Uni<SellerProduct> updateProduct(ClientSession session,
      UserReference userReference,
      ProductReference productReference){
    return validator.validate(userReference)
        .map(ignore -> SellerProductMapper.INSTANCE.from(userReference, productReference))
        .call(sellerProduct -> sellerProductRepository.update(session, sellerProduct));
  }

  public Uni<SuccessResponse> deleteProduct(UserContext userContext, String productId) {
    Document filter = new Document(SellerProduct.FIELD_USER_REFERENCE_ID, userContext.getId())
        .append(SellerProduct.FIELD_PRODUCT_REFERENCE_ID, productId);
    return SellerProduct.find(filter)
        .firstResult()
        .map(Utils.mapTo(SellerProduct.class))
        .flatMap(sellerProduct -> sessionWrapper.getSession()
            .flatMap(session -> productService.delete(session, sellerProduct.getProductReferenceId())
                .call(ignore -> sellerProductRepository.delete(session, productId))
                .replaceWith(Uni.createFrom().publisher(session.commitTransaction()))
                .eventually(session::close))
        ).map(aVoid -> SuccessResponse.toSuccessResponse());
  }

}