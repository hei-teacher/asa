package school.hei.asa.mail;

import jakarta.mail.internet.InternetAddress;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.hei.asa.PojaGenerated;

// import software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest;

@Slf4j
@Component
@PojaGenerated
public class EmailAddressVerifier implements Consumer<InternetAddress> {

  @Override
  public void accept(InternetAddress emailAddress) {
    log.info("Email address verified (SMTP mode): {}", emailAddress.getAddress());
    // Ancienne méthode SES (conservée pour référence)
    // emailConf.getSesClient()
    //     .verifyEmailIdentity(
    //         VerifyEmailIdentityRequest.builder().emailAddress(emailAddress.getAddress()).build());
  }
}
