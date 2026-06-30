package school.hei.asa.service;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.YoTechMailer;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.service.mapper.InternetAddressMapper;

/**
 * Service responsable d'envoyer une alerte aux ACCOUNTANTS quand un worker a moins de {@code
 * asa.low.remaining.days.threshold} jours restants sur son contrat courant.
 */
@Slf4j
@Service
public class LowRemainingDaysAlertService {

  private final YoTechMailer mailer;
  private final String accountants;
  private final InternetAddressMapper internetAddressMapper;
  private final AppSettingsService appSettingsService;

  public LowRemainingDaysAlertService(
      YoTechMailer mailer,
      @Value("${ACCOUNTANTS}") String accountants,
      AppSettingsService appSettingsService,
      InternetAddressMapper internetAddressMapper) {
    this.mailer = mailer;
    this.accountants = accountants;
    this.appSettingsService = appSettingsService;
    this.internetAddressMapper = internetAddressMapper;
  }

  public int getLowRemainingDaysThreshold() {
    return appSettingsService.getLowContractDaysThreshold();
  }

  /**
   * Vérifie si le nombre de jours restants est inférieur au seuil et envoie un mail si c'est le
   * cas.
   *
   * @param worker le worker concerné
   * @param contract le contrat actif
   * @param remainingDays le nombre de jours restants après le pointage courant
   */
  public void checkAndAlert(Worker worker, Contract contract, long remainingDays) {
    int threshold = getLowRemainingDaysThreshold();
    if (remainingDays < threshold) {
      log.warn(
          "Worker '{}' has only {} day(s) remaining on contract starting {}. Threshold={}",
          worker.code(),
          remainingDays,
          contract.entranceInstant(),
          threshold);
      sendAlertToAccountants(worker, contract, remainingDays, threshold);
    }
  }

  private void sendAlertToAccountants(
      Worker worker, Contract contract, long remainingDays, int threshold) {
    var accountantAddresses =
        internetAddressMapper.toInternetAddresses(
            Arrays.stream(this.accountants.split(",")).map(String::trim).toList());

    var subject =
        String.format(
            "ASA - ALERTE : %s a seulement %d jour(s) restant(s) sur son contrat",
            worker.name(), remainingDays);

    var dateFormatter =
        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withZone(java.time.ZoneId.of("UTC"));
    var formattedEntranceDate = dateFormatter.format(contract.entranceInstant());

    var htmlBody =
        String.format(
            """
            <p>Bonjour,</p>
            <p>
              Le worker <strong>%s</strong> (<em>%s</em>) a effectué un pointage
              et il lui reste désormais <strong>%d jour(s)</strong> disponible(s)
              sur son contrat courant (début : %s, durée totale : %d jours).
            </p>
            <p>
              Le seuil d'alerte configuré est de <strong>%d jours</strong>.
              Veuillez prendre les mesures nécessaires.
            </p>
            <p>Cordialement,<br/>ASA</p>
            """,
            worker.name(),
            worker.code(),
            remainingDays,
            formattedEntranceDate,
            contract.duration().toDays(),
            threshold);

    try {
      var to =
          new InternetAddress(
              worker.email() != null ? worker.email() : accountantAddresses.get(0).getAddress());
      mailer.accept(new Email(to, accountantAddresses, List.of(), subject, htmlBody, List.of()));
      log.info("Alert email sent to accountants for worker '{}'", worker.code());
    } catch (AddressException e) {
      log.error("Failed to send low-remaining-days alert email for worker '{}'", worker.code(), e);
    }
  }
}
