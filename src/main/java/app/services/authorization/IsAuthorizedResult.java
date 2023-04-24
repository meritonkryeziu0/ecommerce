package app.services.authorization;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IsAuthorizedResult {
  private boolean isAuthorized;

  public IsAuthorizedResult(){
    this.isAuthorized = false;
  }

  public IsAuthorizedResult(boolean isAuthorized){
    this.isAuthorized = isAuthorized;
  }
}
