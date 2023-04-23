package app.services.authorization;

import io.smallrye.mutiny.Uni;
import lombok.Builder;

@Builder
public class AuthorizationResult {
  private String module;
  private String action;
  private boolean isAuthorized;

  public Uni<AuthorizationResult> asUni(){
    return Uni.createFrom().item(this);
  }
}
