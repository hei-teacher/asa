package school.hei.asa.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.ContractAlertRequested;
import school.hei.asa.model.Worker;

@Slf4j
@Service
public class ContractAlertService {

  private final ContractService contractService;
  private final EventProducer<ContractAlertRequested> eventProducer;
  private final int alertThreshold;

  public ContractAlertService(
      ContractService contractService,
      EventProducer<ContractAlertRequested> eventProducer,
      @Value("${ASA_CONTRACT_ALERT_THRESOLD}") int alertThreshold) {
    this.contractService = contractService;
    this.eventProducer = eventProducer;
    this.alertThreshold = alertThreshold;
  }

  public void sendContractAlert(Worker worker) {
    var remaining = contractService.getRemainingDaysByWorker(worker);
    if (remaining < alertThreshold) {
      try {
        eventProducer.accept(
            List.of(
                ContractAlertRequested.builder()
                    .workerCode(worker.code())
                    .workerEmail(worker.email())
                    .remainingDays(remaining)
                    .build()));
      } catch (Exception e) {
        log.error("Failed to send contract alert event", e);
      }
    }
  }

  public Optional<String> contractAlertMessage(Worker worker, int threshold) {
    var remaining = contractService.getRemainingDaysByWorker(worker);
    if (remaining == 0) {
      return Optional.of("Your contract has no remaining days left.");
    }
    if (remaining < threshold) {
      var plural = remaining > 1 ? "s" : "";
      return Optional.of(
          "Warning: only " + remaining + " day" + plural + " left on your contract.");
    }
    return Optional.empty();
  }
}
