package app.services.accounts;

import app.services.accounts.models.BillingAddress;
import app.services.accounts.models.ShippingAddress;
import app.shared.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class User extends BaseModel {
    public static String FIELD_FIRSTNAME = "firstName";
    public static String FIELD_PHONE_NUMBER = "phoneNumber";
    public static String FIELD_SHIPPING_ADDRESSES = "shippingAddresses";
    public static String FIELD_SHIPPING_ADDRESSES_ID = "_id";
    public static String FILED_BILLING_ADDRESS = "billingAddress";
    public static String FIELD_EMAIL = "email";
    public static String FIELD_HASHEDPASSWORD = "hashedPassword";
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private List<ShippingAddress> shippingAddresses;
    private BillingAddress billingAddress;
    private String role;
    @JsonIgnore
    private String hashedPassword;
    @JsonIgnore private String salt;

}