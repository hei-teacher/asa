package school.hei.asa.service.event;

import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.SendEmailRequested;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.Worker;
import school.hei.asa.service.InvoiceService;
import school.hei.asa.service.WorkerService;

@Service
@AllArgsConstructor
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {
  private static final String INVOICES_FOLDER = "invoices/";

  private Mailer mailer;
  private BucketComponent bucketComponent;
  private InvoiceService invoiceService;
  private WorkerService workerService;

  @SneakyThrows
  @Override
  public void accept(SendEmailRequested event) {
    Worker worker = workerService.findWorkerByCode(event.getWorkerCode());
    InvoiceReference invoiceReference =
        invoiceService.findInvoiceReference(worker, YearMonth.parse(event.getYearMonth())).get();
    var listEmailsWithWorkerEmail = String.format("%s,%s", event.getCc(), worker.email());
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

    File pdf = bucketComponent.download(INVOICES_FOLDER + event.getFileName());
    var email =
        new Email(
            new InternetAddress(event.getTo()),
            internetCc,
            List.of(),
            String.format(
                "ASA INVOICE GENERATED - %s - %s", worker.name(), invoiceReference.yearMonth()),
            String.format(
                "Bonjour, \n Voici la facture de générée de %s  du mois de %s. \n Cordialement,",
                worker.name(), invoiceReference.yearMonth()),
            List.of(pdf));

    mailer.accept(email);
  }
}
