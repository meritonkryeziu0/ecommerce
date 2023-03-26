package app.common;


import lombok.Getter;

import javax.validation.Valid;
import java.util.List;

@Getter
public class ListWrapper<T> {
  @Valid
  private List<T> objects;

  public ListWrapper(List<T> objects) {
    this.objects = objects;
  }
}
