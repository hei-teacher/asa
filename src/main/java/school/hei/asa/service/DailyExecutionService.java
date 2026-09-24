package school.hei.asa.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.repository.DailyExecutionRepository;

@Service
@AllArgsConstructor
public class DailyExecutionService {
  private final DailyExecutionRepository dailyExecutionRepository;
  private final LowRemainingDaysAlertService lowRemainingDaysAlertService;
  private final ContractService contractService;
  private final CareProductCodeSupplier careProductCodeSupplier;

  public void saveAndAlert(DailyExecution dailyExecution) {
    var worker = dailyExecution.worker();
    if (contractService.findActiveContractByWorker(worker).isEmpty()) {
      throw new IllegalStateException("Unable to punch in : you have no active contract.");
    }
    var careProductCode = careProductCodeSupplier.get();
    var requestedDays =
        dailyExecution.executions().stream()
            .distinct()
            .filter(me -> !me.mission().isCare(careProductCode))
            .mapToDouble(MissionExecution::dayPercentage)
            .sum();
    var remainingDays = contractService.getRemainingDaysOnActiveContractOrZero(worker);
    // 1e-9 absorbs double rounding errors (e.g. 0.1 + 0.2 = 0.30000000000000004)
    if (requestedDays > remainingDays + 1e-9) {
      throw new IllegalStateException(
          "Unable to punch in : only " + remainingDays + " day(s) remaining on your contract.");
    }
    dailyExecutionRepository.save(dailyExecution);
    lowRemainingDaysAlertService.sendAlertEmailIfLowRemainingDays(dailyExecution.worker());
  }
}
