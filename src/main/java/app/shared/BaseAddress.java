package app.shared;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BaseAddress {
  public static String FIELD_ID = "_id";
  @NotBlank
  private String street;
  @NotBlank
  private String city;
  @NotBlank
  private String zip;
}