package school.hei.asa.service.event;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static org.reflections.Reflections.log;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.NearOverdueReportNotificationRequested;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Service
public class NearOverdueReportNotificationRequestedService
    implements Consumer<NearOverdueReportNotificationRequested> {

  private final ContractService contractService;
  private final MissionExecutionRepository missionExecutionRepository;
  private final Mailer mailer;
  private final String accountants;
  private final InternetAddressMapper internetAddressMapper;
  private final int maxLatenessForReport;

  public NearOverdueReportNotificationRequestedService(
      ContractService contractService,
      MissionExecutionRepository missionExecutionRepository,
      Mailer mailer,
      @Value("${ACCOUNTANTS}") String accountants,
      @Value("${MAX_LATENESS_REPORT}") int maxLatenessForReport,
      InternetAddressMapper internetAddressMapper) {
    this.contractService = contractService;
    this.missionExecutionRepository = missionExecutionRepository;
    this.mailer = mailer;
    this.accountants = accountants;
    this.internetAddressMapper = internetAddressMapper;
    this.maxLatenessForReport = maxLatenessForReport;
  }

  @Override
  public void accept(NearOverdueReportNotificationRequested nearOverdueNotificationRequested) {
    var maxLatenessReport = Period.ofDays(maxLatenessForReport);
    var dateToVerify =
        nearOverdueNotificationRequested.getVerificationDate().minus(maxLatenessReport);

    if (dateToVerify.getDayOfWeek() != SATURDAY
        && dateToVerify.getDayOfWeek() != SUNDAY
        && dateToVerify.getDayOfWeek() != MONDAY) {
      var workersWhoReported = missionExecutionRepository.findWorkerCodesByDate(dateToVerify);
      var contracts = contractService.findActiveContracts();
      var workersWhoReportedInLate =
          contracts.stream()
              .map(Contract::worker)
              .filter(worker -> !workersWhoReported.contains(worker.code()))
              .toList();

      sendEmailToWorkersWhoDidNotReportYet(workersWhoReportedInLate, dateToVerify);
    }
  }

  public void sendEmailToWorkersWhoDidNotReportYet(List<Worker> receivers, LocalDate date) {
    if (receivers.isEmpty()) {
      log.info("No receiver Found.");
      return;
    }

    var accountants =
        internetAddressMapper.toInternetAddresses(
            Arrays.stream(this.accountants.split(",")).toList());
    var text =
        String.format(
                "Hello, \n"
                        + " This is a reminder that you didn't report your work at the date %s yet. Mind"
                        + " doing it ? \n"
                        + " Best Regards,",
            date);

    log.info("Sending emails to workers...");

    receivers.forEach(
        worker -> {
          try {
            mailer.accept(
                new Email(
                    new InternetAddress(worker.email()),
                    accountants,
                    List.of(),
                    String.format(
                        "ASA - REMINDER TO REPORT THE DATE - %s - %s", date, worker.name()),
                    text,
                    List.of()));
          } catch (AddressException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
