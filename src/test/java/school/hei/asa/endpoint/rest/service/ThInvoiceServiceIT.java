package school.hei.asa.endpoint.rest.service;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;

public class ThInvoiceServiceIT extends FacadeIT {
  @Autowired ThInvoiceService thInvoiceService;
  @MockBean
    Mailer mailer;

  @Test
  void send_invoice_copy_ok() throws IOException {
    var receiver = "dummy@mail,dummy.chan@mail.com";
    var workerName = "dummy";
    var month = "February";
    var email = mock(Email.class);
    File fakeFile = File.createTempFile("temp", ".pdf");
    Files.write(fakeFile.toPath(), new byte[] {1, 2, 3});
    doNothing().when(mailer).accept(email);
    thInvoiceService.sendInvoiceCopy(workerName, receiver, month, fakeFile);
    verify(mailer).accept(any(Email.class));
  }
}
