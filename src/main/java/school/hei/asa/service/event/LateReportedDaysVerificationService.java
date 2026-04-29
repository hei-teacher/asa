package school.hei.asa.service.event;

import static java.time.LocalDate.now;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.LateReportedDaysVerificationRequested;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Service
public class LateReportedDaysVerificationService implements Consumer<LateReportedDaysVerificationRequested> {
  private final ContractService contractService;
  private final MissionExecutionRepository missionExecutionRepository;
  private final Mailer mailer;
  private final String accountants;
  private final InternetAddressMapper emailService;

  public LateReportedDaysVerificationService(
      ContractService contractService,
      MissionExecutionRepository missionExecutionRepository,
      Mailer mailer,
      @Value("${ACCOUNTANTS}") String accountants,
      InternetAddressMapper emailService) {
    this.contractService = contractService;
    this.missionExecutionRepository = missionExecutionRepository;
    this.mailer = mailer;
    this.accountants = accountants;
    this.emailService = emailService;
  }

  @Override
  public void accept(LateReportedDaysVerificationRequested lateReportedDaysVerification) {
    int lateReport = 4;
    var dayToReport = now().minusDays(lateReport);
    var workerWhoReported = missionExecutionRepository.findWorkerCodeByDate(dayToReport);
    var unReportedWorker = extractWorkersWhoDidNotReport(workerWhoReported);
    sendEmailToUnReportedWorkers(
        unReportedWorker.stream().map(contract -> contract.worker().code()).toList(), dayToReport);
  }

  public List<Contract> extractWorkersWhoDidNotReport(List<String> workerCodes) {
    var contracts = contractService.findActiveContract();
    return contracts.stream()
        .filter(contract -> !workerCodes.contains(contract.worker().code()))
        .toList();
  }

  public void sendEmailToUnReportedWorkers(List<String> receivers, LocalDate date) {
    var accountants =
        emailService.toInternetAddresses(Arrays.stream(this.accountants.split(",")).toList());
    var receiverAddresses = emailService.toInternetAddresses(receivers);
    var text =
        String.format(
            "Hello, \n"
                + " This is a reminder that you didn't report your work at the date %s on time. \n"
                + " Best Regards,",
            date);

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
  }
}
