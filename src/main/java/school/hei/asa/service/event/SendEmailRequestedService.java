package school.hei.asa.service.event;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.SendEmailRequested;
import school.hei.asa.mail.Mailer;

@Service
@AllArgsConstructor
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {
  Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(SendEmailRequested sendEmailRequested) {
    mailer.accept(sendEmailRequested.getEmail());
  }
}
