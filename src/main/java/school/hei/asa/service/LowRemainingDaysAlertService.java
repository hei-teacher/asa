package school.hei.asa.service;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.LowRemainingDaysAlertRequested;
import school.hei.asa.model.Worker;
import school.hei.asa.number.DaysFormatter;

@Slf4j
@Service
@AllArgsConstructor
public class LowRemainingDaysAlertService {

  private final EventProducer<LowRemainingDaysAlertRequested> eventProducer;
  private final ContractService contractService;
  private final CalendarService calendarService;

  public Optional<String> checkRemainingDaysAndBuildAlertMessage(Worker worker) {
    var activeContract = contractService.getActiveContractOrThrow(worker);
    var remainingDays = calendarService.getRemainingDaysForContract(worker, activeContract);

    if (!contractService.isBelowThreshold(remainingDays)) {
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
}
