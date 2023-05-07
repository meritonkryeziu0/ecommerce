package app.services.contact;


import app.shared.SuccessResponse;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/contact-us")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MailerResource {
  @Inject
  ReactiveMailer reactiveMailer;

  @POST
  @PermitAll
  public Uni<SuccessResponse> sendEmailUsingReactiveMailer(@Valid MailForm mailForm) {
    return reactiveMailer.send(
        Mail.withText("meritonkryeziu0@gmail.com",
            "Contact Us",
            mailForm.getMessage()
        ).setFrom(mailForm.getEmail())
    ).map(SuccessResponse.success());
  }
}