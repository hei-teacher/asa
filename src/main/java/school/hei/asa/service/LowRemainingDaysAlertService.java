package school.hei.asa.service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Slf4j
@Service
public class LowRemainingDaysAlertService {

  private final Mailer mailer;
  private final String accountants;
  private final InternetAddressMapper internetAddressMapper;
  private final int lowRemainingDaysThreshold;

  public LowRemainingDaysAlertService(
      Mailer mailer,
      @Value("${ACCOUNTANTS}") String accountants,
      @Value("${LOW_CONTRACT_DAYS_THRESOLD}") int lowRemainingDaysThreshold,
      InternetAddressMapper internetAddressMapper) {
    this.mailer = mailer;
    this.accountants = accountants;
    this.lowRemainingDaysThreshold = lowRemainingDaysThreshold;
    this.internetAddressMapper = internetAddressMapper;
  }

  public boolean checkAndAlert(Worker worker, Contract contract, long remainingDays) {
    if (isBelowThreshold(remainingDays)) {
      sendAlertToAccountants(worker, contract, remainingDays);
      return true;
    }
    return false;
  }

  public boolean isBelowThreshold(long remainingDays) {
    return remainingDays < lowRemainingDaysThreshold;
  }

  private void sendAlertToAccountants(Worker worker, Contract contract, long remainingDays) {
    var accountantAddresses =
        internetAddressMapper.toInternetAddresses(
            Arrays.stream(this.accountants.split(",")).map(String::trim).toList());

    var subject =
        String.format(
            "ASA - ALERT: %s has only %d day(s) remaining on their contract",
            worker.name(), remainingDays);

    var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.of("UTC"));
    var formattedEntranceDate = dateFormatter.format(contract.entranceInstant());

    var htmlBody =
        String.format(
            """
            <p>Hello,</p>
            <p>
              Worker <strong>%s</strong> (<em>%s</em>) has logged a check-in
              and now has <strong>%d day(s)</strong> remaining
              on their current contract (start: %s, total duration: %d days).
            </p>
            <p>
              The configured alert threshold is <strong>%d days</strong>.
              Please take the necessary action.
            </p>
            <p>Regards,<br/>ASA</p>
            """,
            worker.name(),
            worker.code(),
            remainingDays,
            formattedEntranceDate,
            contract.duration().toDays(),
            lowRemainingDaysThreshold);

    var to = accountantAddresses.getFirst();
    var cc = accountantAddresses.stream().skip(1).toList();

    log.info("Sending alert email to accountants for worker '{}'", worker.code());
    mailer.accept(new Email(to, cc, List.of(), subject, htmlBody, List.of()));
    log.info("Alert email sent to accountants for worker '{}'", worker.code());
  }
}
