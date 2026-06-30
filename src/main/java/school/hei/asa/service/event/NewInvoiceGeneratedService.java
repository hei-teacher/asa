package school.hei.asa.service.event;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;
import school.hei.asa.file.bucket.BucketPort;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.service.InvoiceService;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Service
public class NewInvoiceGeneratedService implements Consumer<NewInvoiceGenerated> {
  private static final String INVOICES_FOLDER = "invoices/";
  private final String accountants;
  private final Mailer mailer;
  private final BucketPort bucketComponent;
  private final InvoiceService invoiceService;
  private final InternetAddressMapper emailService;

  public NewInvoiceGeneratedService(
      @Value("${ACCOUNTANTS}") String accountants,
      Mailer mailer,
      BucketPort bucketComponent,
      InvoiceService invoiceService,
      InternetAddressMapper emailService) {
    this.accountants = accountants;
    this.mailer = mailer;
    this.bucketComponent = bucketComponent;
    this.invoiceService = invoiceService;
    this.emailService = emailService;
  }

  @Override
  public void accept(NewInvoiceGenerated event) {
    InvoiceReference invoiceReference = invoiceService.getInvoiceReference(event.getInvoiceId());

    var listEmailsWithWorkerEmail =
        String.format("%s,%s", accountants, invoiceReference.worker().email());
    var emailList = Arrays.asList(listEmailsWithWorkerEmail.split(","));
    var internetAddresses = emailService.toInternetAddresses(emailList);

    var fileName =
        invoiceService.getInvoiceBucketKey(invoiceReference.worker(), invoiceReference.yearMonth());
    File pdf = bucketComponent.download(INVOICES_FOLDER + fileName);
    var email =
        new Email(
            internetAddresses.getFirst(),
            internetAddresses.stream().skip(1).toList(),
            List.of(),
            String.format(
                "ASA INVOICE GENERATED - %s - %s",
                invoiceReference.worker().name(), invoiceReference.yearMonth()),
            String.format(
                "Hello,\n"
                    + " Please find attached the generated invoice for %s for the month of %s.Best"
                    + " regards,",
                invoiceReference.worker().name(), invoiceReference.yearMonth()),
            List.of(pdf));

    mailer.accept(email);
  }
}
