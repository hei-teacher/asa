package school.hei.asa.service;

import static java.util.Locale.US;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.LowRemainingDaysAlertRequested;
import school.hei.asa.model.Worker;

@Slf4j
@Service
public class LowRemainingDaysAlertService {

  private final ContractService contractService;
  private final EventProducer<LowRemainingDaysAlertRequested> eventProducer;
  private final int lowRemainingDaysThreshold;

  public LowRemainingDaysAlertService(
      ContractService contractService,
      EventProducer<LowRemainingDaysAlertRequested> eventProducer,
      @Value("${LOW_REMAINING_DAYS_THRESHOLD}") int lowRemainingDaysThreshold) {
    this.contractService = contractService;
    this.eventProducer = eventProducer;
    this.lowRemainingDaysThreshold = lowRemainingDaysThreshold;
  }

  public Optional<String> checkRemainingDaysAndBuildAlertMessage(Worker worker) {
    var remainingDays = contractService.getRemainingDaysOnActiveContractOrZero(worker);

    if (remainingDays == 0 || remainingDays >= lowRemainingDaysThreshold) {
      return Optional.empty();
    }

    log.info("Requesting alert email to accountants for worker '{}'", worker.code());
    eventProducer.accept(
        List.of(
            LowRemainingDaysAlertRequested.builder()
                .workerCode(worker.code())
                .remainingDays((int) remainingDays)
                .build()));

    return Optional.of(
        "Please note : You have " + formatDays(remainingDays) + " day(s) left on your contract !");
  }

  private static String formatDays(double days) {
    return days == (long) days ? String.valueOf((long) days) : String.format(US, "%.1f", days);
  }
}
