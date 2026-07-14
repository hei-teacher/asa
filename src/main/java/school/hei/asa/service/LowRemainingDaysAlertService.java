package school.hei.asa.service;

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

  private final EventProducer<LowRemainingDaysAlertRequested> eventProducer;
  private final int lowRemainingDaysThreshold;
  private final ContractService contractService;

  public LowRemainingDaysAlertService(
      EventProducer<LowRemainingDaysAlertRequested> eventProducer,
      @Value("${LOW_CONTRACT_DAYS_THRESOLD}") int lowRemainingDaysThreshold,
      ContractService contractService) {
    this.eventProducer = eventProducer;
    this.lowRemainingDaysThreshold = lowRemainingDaysThreshold;
    this.contractService = contractService;
  }

  public Optional<String> checkRemainingDaysAndBuildAlertMessage(Worker worker) {
    var activeContract = contractService.getActiveContractOrThrow(worker);
    var remainingDays = contractService.getRemainingDaysForContract(worker, activeContract);

    boolean alertSent = checkAndAlert(worker, (long) remainingDays);

    return alertSent
        ? Optional.of(
            "Please note : You have " + (long) remainingDays + " day(s) left on your contract !")
        : Optional.empty();
  }

  public boolean checkAndAlert(Worker worker, long remainingDays) {
    if (isBelowThreshold(remainingDays)) {
      requestAlertEmail(worker, remainingDays);
      return true;
    }
    return false;
  }

  public boolean isBelowThreshold(long remainingDays) {
    return remainingDays < lowRemainingDaysThreshold;
  }

  private void requestAlertEmail(Worker worker, long remainingDays) {
    log.info("Requesting alert email to accountants for worker '{}'", worker.code());
    var event =
        LowRemainingDaysAlertRequested.builder()
            .workerCode(worker.code())
            .remainingDays(remainingDays)
            .build();
    eventProducer.accept(List.of(event));
  }
}
