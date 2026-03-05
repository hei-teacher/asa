package school.hei.asa.service.event;

import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.service.InvoiceService;

@Service
@RequiredArgsConstructor
public class NewInvoiceGeneratedService implements Consumer<NewInvoiceGenerated> {
  private static final String INVOICES_FOLDER = "invoices/";

  @Value("${ACCOUNTANTS}")
  private String accountants;

  private final Mailer mailer;
  private final BucketComponent bucketComponent;
  private final InvoiceService invoiceService;

  @SneakyThrows
  @Override
  public void accept(NewInvoiceGenerated event) {
    InvoiceReference invoiceReference = invoiceService.getInvoiceReference(event.getInvoiceId());
    var fileName =
        invoiceService.getInvoiceBucketKey(invoiceReference.worker(), invoiceReference.yearMonth());
    var listEmailsWithWorkerEmail =
        String.format("%s,%s", accountants, invoiceReference.worker().email());
    var listEmails = Arrays.stream(listEmailsWithWorkerEmail.split(",")).toList();
    var internetCc =
        listEmails.stream()
            .skip(1)
            .map(
                mail -> {
                  try {
                    return new InternetAddress(mail);
                  } catch (Exception e) {
                    throw new RuntimeException(e);
                  }
                })
            .toList();

    File pdf = bucketComponent.download(INVOICES_FOLDER + fileName);
    var email =
        new Email(
            new InternetAddress(listEmails.getFirst()),
            internetCc,
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
}
