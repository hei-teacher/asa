package school.hei.asa.service.event;

import static org.reflections.Reflections.log;

import jakarta.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.LowRemainingDaysAlertRequested;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Service
public class LowRemainingDaysAlertRequestedService
    implements Consumer<LowRemainingDaysAlertRequested> {

  private final Mailer mailer;
  private final String accountants;
  private final InternetAddressMapper internetAddressMapper;

  public LowRemainingDaysAlertRequestedService(
      Mailer mailer,
      @Value("${ACCOUNTANTS}") String accountants,
      InternetAddressMapper internetAddressMapper) {
    this.mailer = mailer;
    this.accountants = accountants;
    this.internetAddressMapper = internetAddressMapper;
  }

  @Override
  public void accept(LowRemainingDaysAlertRequested event) {
    var accountantAddresses =
        internetAddressMapper.toInternetAddresses(
            Arrays.stream(this.accountants.split(",")).toList());

    sendAlertToAccountants(accountantAddresses, event.getWorkerCode(), event.getRemainingDays());
  }

  private void sendAlertToAccountants(
      List<InternetAddress> accountantAddresses, String workerCode, int remainingDays) {
    if (accountantAddresses.isEmpty()) {
      log.info("No accountant address found. Skipping alert email.");
      return;
    }

    var subject = String.format("ASA - ALERT: low remaining days - Worker %s", workerCode);
    var body =
        String.format(
            "Hello,\n\n"
                + "Worker %s has only %d day(s) remaining on their contract.\n\n"
                + "Please take the necessary action.\n\n"
                + "Best regards,\n"
                + "ASA",
            workerCode, remainingDays);

    log.info(
        "Sending low remaining days alert to accountants for worker={}, remainingDays={}",
        workerCode,
        remainingDays);

    try {
      mailer.accept(
          new Email(
              accountantAddresses.get(0),
              accountantAddresses.subList(1, accountantAddresses.size()),
              List.of(),
              subject,
              body,
              List.of()));
    } catch (Exception e) {
      throw new RuntimeException("Failed to send low remaining days alert email", e);
    }
  }
}
