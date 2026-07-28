package school.hei.asa.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.repository.DailyExecutionRepository;

@Service
@AllArgsConstructor
public class DailyExecutionService {
  private final DailyExecutionRepository dailyExecutionRepository;
  private final LowRemainingDaysAlertService lowRemainingDaysAlertService;

  public void verifyAndSave(DailyExecution dailyExecution) {
    dailyExecutionRepository.save(dailyExecution);
    lowRemainingDaysAlertService.sendAlertEmailIfLowRemainingDays(dailyExecution.worker());
  }
}
