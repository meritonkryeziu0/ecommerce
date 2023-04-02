package app.services.order.models;


import app.services.accounts.models.UserReference;
import app.services.product.models.ProductReference;
import app.shared.BaseAddress;
import app.shared.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Order extends BaseModel {

    public static String FIELD_STATUS = "status";
    public static String FIELD_TRACKINGNUMBER = "tracking.trackingNumber";
    public static String FIELD_USER_ID = "userReference._id";

    private String status;
    private UserReference userReference;
    private BaseAddress shippingAddress;
    private PaymentMethod paymentMethod;
    private List<ProductReference> products;
    private Double total;
    private String shipmentType;
}