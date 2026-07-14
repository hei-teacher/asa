package school.hei.asa.mail;

import static org.mockito.Mockito.*;

import jakarta.mail.internet.InternetAddress;
import org.junit.jupiter.api.Test;

class EmailAddressVerifierTest {

  @Test
  void accept_verifies_email() throws Exception {
    var emailConf = mock(EmailConf.class);
    var sesClient = mock(software.amazon.awssdk.services.ses.SesClient.class);
    when(emailConf.getSesClient()).thenReturn(sesClient);

    var verifier = new EmailAddressVerifier(emailConf);
    var address = new InternetAddress("test@example.com");

    verifier.accept(address);

    verify(sesClient)
        .verifyEmailIdentity(
            any(software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest.class));
  }
}
