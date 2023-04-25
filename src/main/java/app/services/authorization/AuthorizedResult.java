package app.services.authorization;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorizedResult {
  private boolean isAuthorized;

  public AuthorizedResult(){
    this.isAuthorized = false;
  }

  public AuthorizedResult(boolean isAuthorized){
    this.isAuthorized = isAuthorized;
  }
}
