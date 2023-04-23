package app.services.accounts.models;

import app.mongodb.MongoCollections;
import app.shared.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@MongoEntity(collection = MongoCollections.USERS_COLLECTION)
public class User extends BaseModel {
  public static String FIELD_FIRSTNAME = "firstName";
  public static String FIELD_PHONE_NUMBER = "phoneNumber";
  public static String FIELD_SHIPPING_ADDRESSES = "shippingAddresses";
  public static String FIELD_SHIPPING_ADDRESSES_ID = "shippingAddresses._id";
  public static String FILED_BILLING_ADDRESS = "billingAddress";
  public static String FIELD_EMAIL = "email";
  public static String FIELD_HASHEDPASSWORD = "hashedPassword";
  public static String FIELD_STATE = "state";
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private List<ShippingAddress> shippingAddresses;
  private BillingAddress billingAddress;
//  private List<Roles> roles;
  private Roles role;
  private String state;
  @JsonIgnore
  private String hashedPassword;
  @JsonIgnore
  private String salt;

//  public void addRole(Roles role) {
//    this.roles.add(role);
//  }

//  public Set<String> fetchRolesString() {
//    return this.roles.stream().map(Enum::name).collect(Collectors.toSet());
//  }

}