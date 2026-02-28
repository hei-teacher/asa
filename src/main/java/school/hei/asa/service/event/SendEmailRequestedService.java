package school.hei.asa.service.event;

import jakarta.mail.internet.InternetAddress;
import java.io.File;
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

@Service
@AllArgsConstructor
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {
  Mailer mailer;

  private final BucketComponent bucketComponent;

  @SneakyThrows
  @Override
  public void accept(SendEmailRequested event) {
    File tempFile = File.createTempFile("invoice", ".pdf");
    var file = event.getS3Key() + tempFile;
    bucketComponent.download(event.getS3Key());

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
            List.of(tempFile));

    mailer.accept(email);
  }
}
