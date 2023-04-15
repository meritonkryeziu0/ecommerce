package app.services.manufacturer.models;

import app.shared.BaseAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerReference {
  public String _id;
  private String name;
  private BaseAddress address;

}