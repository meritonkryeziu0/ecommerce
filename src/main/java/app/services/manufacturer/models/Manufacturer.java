package app.services.manufacturer.models;

import app.shared.BaseAddress;
import app.shared.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Manufacturer extends BaseModel {
  public static String FIELD_ID = "_id";
  public static String FIELD_NAME = "name";

  private String name;
  private BaseAddress address;

}