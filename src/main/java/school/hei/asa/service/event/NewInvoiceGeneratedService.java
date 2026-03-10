package school.hei.asa.service.event;

import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.service.InvoiceService;

@Service
public class NewInvoiceGeneratedService implements Consumer<NewInvoiceGenerated> {
  private static final String INVOICES_FOLDER = "invoices/";
  private final String accountants;
  private final Mailer mailer;
  private final BucketComponent bucketComponent;
  private final InvoiceService invoiceService;

  public NewInvoiceGeneratedService(
      @Value("${ACCOUNTANTS}") String accountants,
      Mailer mailer,
      BucketComponent bucketComponent,
      InvoiceService invoiceService) {
    this.accountants = accountants;
    this.mailer = mailer;
    this.bucketComponent = bucketComponent;
    this.invoiceService = invoiceService;
  }

  @Override
  public void accept(NewInvoiceGenerated event) {
    InvoiceReference invoiceReference = invoiceService.getInvoiceReference(event.getInvoiceId());
    var fileName =
        invoiceService.getInvoiceBucketKey(invoiceReference.worker(), invoiceReference.yearMonth());
    var listEmailsWithWorkerEmail =
        String.format("%s,%s", accountants, invoiceReference.worker().email());

    // converting string to a list
    var listEmails = new ArrayList<>(Arrays.stream(listEmailsWithWorkerEmail.split(",")).toList());

    // converting every items in the list to Internet Address
    var internetAddressesCc = toInternetAddresses(listEmails);

    // similar to getFirst for the attribute "to"  in Email,
    // but using removeFirst so I can get a list without the first item, excluding the "to" receiver

    var mainReceiver = internetAddressesCc.removeFirst();

    File pdf = bucketComponent.download(INVOICES_FOLDER + fileName);
    var email =
        new Email(
            // the "to" as the main receiver
            mainReceiver,
            // here, sending emails to the list whithout sending it again to "to"
            internetAddressesCc,
            List.of(),
            String.format(
                "ASA INVOICE GENERATED - %s - %s",
                invoiceReference.worker().name(), invoiceReference.yearMonth()),
            String.format(
                "Bonjour, \n Voici la facture de générée de %s  du mois de %s. \n Cordialement,",
                invoiceReference.worker().name(), invoiceReference.yearMonth()),
            List.of(pdf));

    mailer.accept(email);
  }

  public List<InternetAddress> toInternetAddresses(List<String> emails) {
    return emails.stream()
        .map(
            mail -> {
              try {
                return new InternetAddress(mail);
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            })
        .toList();
  }
}
