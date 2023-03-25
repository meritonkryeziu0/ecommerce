package app.shared;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResponse {
  private String message;
  private int statusCode;


  public static SuccessResponse toSuccessResponse() {
    return new SuccessResponse("Success", 200);
  }

  public SuccessResponse(String message, int statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

}
