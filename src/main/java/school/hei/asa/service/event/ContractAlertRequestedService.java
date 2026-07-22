package school.hei.asa.service.event;

import jakarta.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.concurrency.Workers;
import school.hei.asa.endpoint.event.model.ContractAlertRequested;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Service
public class ContractAlertRequestedService implements Consumer<ContractAlertRequested> {

  private final Mailer mailer;
  private final InternetAddressMapper internetAddressMapper;
  private final Workers workers;
  private final String accountants;

  public ContractAlertRequestedService(
      Mailer mailer,
      InternetAddressMapper internetAddressMapper,
      Workers workers,
      @Value("${ACCOUNTANTS}") String accountants) {
    this.mailer = mailer;
    this.internetAddressMapper = internetAddressMapper;
    this.workers = workers;
    this.accountants = accountants;
  }

  @Override
  public void accept(ContractAlertRequested event) {
    var accountantEmails = toAccountantEmails();
    if (accountantEmails.isEmpty()) {
      return;
    }

    var email = buildAlertEmail(event, accountantEmails);
    workers.invokeAll(
        List.of(
            () -> {
              mailer.accept(email);
              return null;
            }));
  }

  private List<InternetAddress> toAccountantEmails() {
    return internetAddressMapper.toInternetAddresses(Arrays.asList(accountants.split(",")));
  }

  private Email buildAlertEmail(
      ContractAlertRequested event, List<InternetAddress> accountantEmails) {
    var remaining = (long) event.getRemainingDays();
    var plural = remaining > 1 ? "s" : "";

    var subject =
        "ASA - CONTRACT ALERT - " + event.getWorkerName() + " - Only " + remaining + " days left";

    var body =
        "Hello,<br><br>"
            + "The contract of <b>"
            + event.getWorkerName()
            + "</b> ("
            + event.getWorkerEmail()
            + ") is about to expire.<br>"
            + "Only <b>"
            + remaining
            + "</b> day"
            + plural
            + " left on the contract.<br><br>"
            + "Best regards,<br>ASA";

    return new Email(
        accountantEmails.getFirst(),
        accountantEmails.size() > 1
            ? accountantEmails.subList(1, accountantEmails.size())
            : List.of(),
        List.of(),
        subject,
        body,
        List.of());
  }
}
