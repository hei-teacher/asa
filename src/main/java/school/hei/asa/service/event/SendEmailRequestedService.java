package school.hei.asa.service.event;

import jakarta.mail.internet.InternetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.SendEmailRequested;
import school.hei.asa.endpoint.rest.service.InvoicePDFGenerator;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;

@Service
@AllArgsConstructor
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {
  Mailer mailer;
  private final InvoicePDFGenerator invoicePDFGenerator;

  @SneakyThrows
  @Override
  public void accept(SendEmailRequested event) {

    var file = invoicePDFGenerator.apply(event.getWorker(), event.getInvoiceForm(), "invoice");
    Path path =
        Paths.get(
            String.format(
                "FACTURE_%s_%s.pdf", event.getWorker().name().toUpperCase(), event.getMonth()));
    var listEmails = Arrays.stream(event.getCc().split(",")).toList();
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

    var email =
        new Email(
            new InternetAddress(event.getTo()),
            internetCc,
            List.of(),
            event.getSubject(),
            event.getBody(),
            List.of(file));

    mailer.accept(email);
  }
}
