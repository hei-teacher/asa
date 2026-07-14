package school.hei.asa.service.event;

import static org.mockito.Mockito.*;

import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.Worker;
import school.hei.asa.service.InvoiceService;
import school.hei.asa.service.mapper.InternetAddressMapper;

class NewInvoiceGeneratedServiceTest {

  @Test
  void accept_sends_email_with_invoice() throws Exception {
    var mailer = mock(Mailer.class);
    var bucketComponent = mock(BucketComponent.class);
    var invoiceService = mock(InvoiceService.class);
    var emailService = mock(InternetAddressMapper.class);
    var service =
        new NewInvoiceGeneratedService(
            "accountant@test.com", mailer, bucketComponent, invoiceService, emailService);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var invoiceRef = new InvoiceReference("inv-001", YearMonth.of(2025, 6), 1, worker);
    when(invoiceService.getInvoiceReference("inv-001")).thenReturn(invoiceRef);
    when(invoiceService.getInvoiceBucketKey(worker, YearMonth.of(2025, 6)))
        .thenReturn("invoice-key");
    when(bucketComponent.download("invoices/invoice-key"))
        .thenReturn(File.createTempFile("invoice", ".pdf"));
    when(emailService.toInternetAddresses(anyList()))
        .thenReturn(
            List.of(new InternetAddress("acc@test.com"), new InternetAddress("john@test.com")));

    var event = new NewInvoiceGenerated("inv-001");
    service.accept(event);

    verify(mailer).accept(any(Email.class));
  }
}
