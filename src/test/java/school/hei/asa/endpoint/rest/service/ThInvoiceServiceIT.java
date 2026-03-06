package school.hei.asa.endpoint.rest.service;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;
import school.hei.asa.service.InvoiceService;

public class ThInvoiceServiceIT extends FacadeIT {
  @Autowired ThInvoiceService thInvoiceService;
  @MockBean EventProducer<NewInvoiceGenerated> eventProducer;
  @Autowired private InvoiceService invoiceService;

  @Test
  void send_invoice_copy_ok() throws IOException {
    var event = mock(NewInvoiceGenerated.class);
    doNothing().when(eventProducer).accept(List.of(event));
    invoiceService.sendGenerateInvoiceEvent("invoiceId");
    verify(eventProducer).accept(anyList());
  }
}
