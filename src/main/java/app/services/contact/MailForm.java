package app.services.contact;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class MailForm {
  @NotBlank
  @Size(max = 255)
  private String name;
  @Email
  @NotBlank
  private String email;
  @NotBlank
  @Size(max = 1023)
  private String message;
}