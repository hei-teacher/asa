package school.hei.asa.endpoint.rest.service;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.Worker;
import school.hei.asa.service.InvoiceService;

public class ThInvoiceServiceIT extends FacadeIT {
  @Autowired ThInvoiceService thInvoiceService;
  @MockBean EventProducer<NewInvoiceGenerated> eventProducer;
  @Autowired private InvoiceService invoiceService;

  @Test
  void send_invoice_copy_ok() throws IOException {
    var event = mock(NewInvoiceGenerated.class);
    doNothing().when(eventProducer).accept(List.of(event));
    var worker = new Worker("W-101", "John", "", "", "", "", "", "", null, null);
    var document =
        new InvoiceForm(
            "invoiceId",
            YearMonth.of(2025, 8),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    invoiceService.sendGeneratedDocumentEvent(worker, document, "invoices/whatever.pdf");

    verify(eventProducer).accept(anyList());
  }
}
