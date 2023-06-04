package app.shared;

import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Getter
@Setter
public class SuccessResponse {
  private String message;
  private int statusCode;


  public SuccessResponse(String message, int statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

  public static SuccessResponse toSuccessResponse() {
    return new SuccessResponse("Success", 200);
  }

  public static <T> Function<T, SuccessResponse> success() {
    return item -> new SuccessResponse("Success", 200);
  }

  public static <T> Function<T, Uni<? extends SuccessResponse>> successAsUni() {
    return item -> Uni.createFrom().item(new SuccessResponse("Success", 200));
  }

}
