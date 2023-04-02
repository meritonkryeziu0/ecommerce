package app.services.shoppingCart.models;

import app.services.accounts.User;
import app.services.accounts.models.UserReference;
import app.services.product.models.ProductReference;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class CreateShoppingCart {
    private UserReference userReference;
    @Valid
    List<ProductReference> products;
    private Double total;

}
