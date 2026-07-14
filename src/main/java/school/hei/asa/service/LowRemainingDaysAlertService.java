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
    var remainingDays = (long) contractService.getRemainingDaysForContract(worker, activeContract);

    if (!isBelowThreshold(remainingDays)) {
      return Optional.empty();
    }

    log.info("Requesting alert email to accountants for worker '{}'", worker.code());
    eventProducer.accept(
        List.of(
            LowRemainingDaysAlertRequested.builder()
                .workerCode(worker.code())
                .remainingDays(remainingDays)
                .build()));

    return Optional.of(
        "Please note : You have " + remainingDays + " day(s) left on your contract !");
  }

  public boolean isBelowThreshold(long remainingDays) {
    return remainingDays < lowRemainingDaysThreshold;
  }
}
