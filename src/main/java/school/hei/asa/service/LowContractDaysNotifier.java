package school.hei.asa.service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.YoTechMailer;
import school.hei.asa.model.Worker;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Slf4j
@Service
public class LowContractDaysNotifier {

  private final String accountants;
  private final YoTechMailer mailer;
  private final InternetAddressMapper internetAddressMapper;

  public LowContractDaysNotifier(
      @Value("${ACCOUNTANTS}") String accountants,
      YoTechMailer mailer,
      InternetAddressMapper internetAddressMapper) {
    this.accountants = accountants;
    this.mailer = mailer;
    this.internetAddressMapper = internetAddressMapper;
  }

  public void notifyLowContractDays(Worker worker, double remainingDays, int threshold) {
    var emailList = Arrays.asList(accountants.split(","));
    var internetAddresses = internetAddressMapper.toInternetAddresses(emailList);

    var subject =
        String.format("ASA - Contrat bientot epuise - %s (%s)", worker.name(), worker.code());
    var body =
        String.format(
            Locale.US,
            "Bonjour,%n%n"
                + "Le contrat du collaborateur %s (code: %s) arrive bientot a son terme.%n"
                + "Jours restants sur le contrat : %.1f.%n"
                + "Seuil d'alerte configure : %d jour(s).%n%n"
                + "Merci de prendre les dispositions necessaires.%n%n"
                + "Cordialement,%nASA",
            worker.name(),
            worker.code(),
            remainingDays,
            threshold);

    var email =
        new Email(
            internetAddresses.getFirst(),
            internetAddresses.stream().skip(1).toList(),
            List.of(),
            subject,
            body,
            List.of());

    mailer.accept(email);
    log.info(
        "Low contract days alert sent to ACCOUNTANTS for worker {} (remainingDays={},"
            + " threshold={})",
        worker.code(),
        remainingDays,
        threshold);
  }
}
