package school.hei.asa.endpoint.rest.service;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.SendEmailRequested;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.Worker;

public class ThInvoiceServiceIT extends FacadeIT {
  @Autowired ThInvoiceService thInvoiceService;
  @MockBean EventProducer<SendEmailRequested> eventProducer;

  @Test
  void send_invoice_copy_ok() throws IOException {
    var receiver = "dummy@mail,dummy.chan@mail.com";
    var worker = mock(Worker.class);
    var form = mock(ThInvoiceForm.class);
    var email = mock(SendEmailRequested.class);
    File fakeFile = File.createTempFile("temp", ".pdf");
    Files.write(fakeFile.toPath(), new byte[] {1, 2, 3});
    doNothing().when(eventProducer).accept(List.of(email));
    thInvoiceService.sendInvoiceCopy(worker, receiver, form);
    verify(eventProducer).accept(anyList());
  }
}
