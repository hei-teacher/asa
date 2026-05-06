package school.hei.asa.service.event;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static org.reflections.Reflections.log;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.LateReportedDaysVerificationRequested;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Service
public class LateReportedDaysVerificationRequestedService
    implements Consumer<LateReportedDaysVerificationRequested> {

  private final ContractService contractService;
  private final MissionExecutionRepository missionExecutionRepository;
  private final Mailer mailer;
  private final String accountants;
  private final InternetAddressMapper emailService;
  private final int maxLatenessForReport;

  public LateReportedDaysVerificationRequestedService(
      ContractService contractService,
      MissionExecutionRepository missionExecutionRepository,
      Mailer mailer,
      @Value("${ACCOUNTANTS}") String accountants,
      @Value("${MAX_LATENESS_REPORT}") int maxLatenessForReport,
      InternetAddressMapper emailService) {
    this.contractService = contractService;
    this.missionExecutionRepository = missionExecutionRepository;
    this.mailer = mailer;
    this.accountants = accountants;
    this.emailService = emailService;
    this.maxLatenessForReport = maxLatenessForReport;
  }

  @Override
  public void accept(LateReportedDaysVerificationRequested lateReportedDaysVerification) {
    var maxLatenessReport = Period.ofDays(maxLatenessForReport);
    var dateToVerify = lateReportedDaysVerification.getVerificationDate().minus(maxLatenessReport);

    if (dateToVerify.getDayOfWeek() != SATURDAY
        && dateToVerify.getDayOfWeek() != SUNDAY
        && dateToVerify.getDayOfWeek() != MONDAY) {
      var workerWhoReported = missionExecutionRepository.findWorkerCodeByDate(dateToVerify);
      var unReportedWorker =
          extractWorkersWhoDidNotReport(workerWhoReported).stream().map(Worker::email).toList();
      sendEmailToUnReportedWorkers(unReportedWorker, dateToVerify);
    }
  }

  public List<Worker> extractWorkersWhoDidNotReport(List<String> workerCodes) {
    var contracts = contractService.findActiveContract();
    return contracts.stream()
        .filter(contract -> !workerCodes.contains(contract.worker().code()))
        .map(Contract::worker)
        .toList();
  }

  public void sendEmailToUnReportedWorkers(List<String> receivers, LocalDate date) {

    if (!receivers.isEmpty()) {
      var accountants =
          emailService.toInternetAddresses(Arrays.stream(this.accountants.split(",")).toList());
      var receiverAddresses = emailService.toInternetAddresses(receivers);
      var text =
          String.format(
              "Hello, \n"
                  + " This is a reminder that you didn't report your work at the date %s on time."
                  + " \n"
                  + " Best Regards,",
              date);

      log.info("Sending emails to workers...");
      receiverAddresses.forEach(
          receiver -> {
            mailer.accept(
                new Email(
                    receiver,
                    accountants,
                    List.of(),
                    String.format("ASA - LATE REPORTED WORK ON %s", date),
                    text,
                    List.of()));
          });
    } else {
      log.info("No receivers found");
    }
  }

  public List<Worker> getWorkersWhoReportedLate(LocalDate date) {
    var missionExecutions = missionExecutionRepository.findByDate(date);

    return missionExecutions.stream()
        .filter(
            me ->
                ChronoUnit.DAYS.between(
                        date, me.reportedAt().atZone(ZoneId.systemDefault()).toLocalDate())
                    > 3)
        .map(MissionExecution::worker)
        .toList();
  }
}
