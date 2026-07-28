package school.hei.asa.service.event;

import jakarta.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.ContractAlertRequested;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Service
public class ContractAlertRequestedService implements Consumer<ContractAlertRequested> {

  private final Mailer mailer;
  private final InternetAddressMapper internetAddressMapper;
  private final String accountants;

  public ContractAlertRequestedService(
      Mailer mailer,
      InternetAddressMapper internetAddressMapper,
      @Value("${ACCOUNTANTS}") String accountants) {
    this.mailer = mailer;
    this.internetAddressMapper = internetAddressMapper;
    this.accountants = accountants;
  }

  @Override
  public void accept(ContractAlertRequested event) {
    var accountantEmails =
        internetAddressMapper.toInternetAddresses(Arrays.asList(accountants.split(",")));
    if (accountantEmails.isEmpty()) {
      return;
    }

    var remaining = (long) event.getRemainingDays();
    var plural = remaining > 1 ? "s" : "";

    var email =
        new Email(
            accountantEmails.getFirst(),
            accountantEmails.size() > 1
                ? accountantEmails.subList(1, accountantEmails.size())
                : List.of(),
            List.of(),
            "ASA - CONTRACT ALERT - "
                + event.getWorkerCode()
                + " - Only "
                + remaining
                + " days left",
            "Hello,<br><br>"
                + "The contract of <b>"
                + event.getWorkerCode()
                + "</b> ("
                + event.getWorkerEmail()
                + ") is about to expire.<br>"
                + "Only <b>"
                + remaining
                + "</b> day"
                + plural
                + " left on the contract.<br><br>"
                + "Best regards,<br>ASA",
            List.of());

    mailer.accept(email);
  }
}
