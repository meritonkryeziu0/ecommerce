package app.services.shoppingcart.models;

import app.services.product.models.ProductReference;
//import app.services.user.models.User;
//import app.services.user.models.UserReference;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class CreateShoppingCart {
//    private UserReference userReference;
    @Valid
    List<ProductReference> products;
    private Double total;

//    public CreateShoppingCart(User user) {
//        this.userReference = new UserReference(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
//    }
}
