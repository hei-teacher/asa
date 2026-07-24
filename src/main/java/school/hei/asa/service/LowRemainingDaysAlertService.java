package school.hei.asa.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.LowRemainingDaysAlertRequested;
import school.hei.asa.model.Worker;
import school.hei.asa.number.DaysFormatter;

@Slf4j
@Service
public class LowRemainingDaysAlertService {

  private final EventProducer<LowRemainingDaysAlertRequested> eventProducer;
  private final ContractService contractService;
  private final int lowRemainingDaysThreshold;

  public LowRemainingDaysAlertService(
      EventProducer<LowRemainingDaysAlertRequested> eventProducer,
      ContractService contractService,
      @Value("${LOW_CONTRACT_DAYS_THRESOLD}") int lowRemainingDaysThreshold) {
    this.eventProducer = eventProducer;
    this.contractService = contractService;
    this.lowRemainingDaysThreshold = lowRemainingDaysThreshold;
  }

  public Optional<String> checkRemainingDaysAndBuildAlertMessage(Worker worker) {
    if (contractService.findActiveContract(worker).isEmpty()) {
      return Optional.empty();
    }

    var remainingDays = contractService.getRemainingDaysOnActiveContractOrZero(worker);

    if (remainingDays <= 0 || !isBelowThreshold(remainingDays)) {
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
        "Please note : You have "
            + DaysFormatter.format(remainingDays)
            + " day(s) left on your contract !");
  }

  public boolean isBelowThreshold(double remainingDays) {
    return remainingDays > 0 && remainingDays < lowRemainingDaysThreshold;
  }
}
