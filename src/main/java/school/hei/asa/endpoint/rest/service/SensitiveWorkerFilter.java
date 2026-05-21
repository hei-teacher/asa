package school.hei.asa.endpoint.rest.service;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;

@Service
@Slf4j
public class SensitiveWorkerFilter {
  private final List<String> sensitiveWorkerCodes;

  public SensitiveWorkerFilter(@Value("${SENSITIVE_WORKERS_CODES}") String sensitiveWorkerCodes) {
    this.sensitiveWorkerCodes = Arrays.stream(sensitiveWorkerCodes.split(",")).toList();
  }

  public List<Worker> filterSensitiveWorkers(
      List<Worker> workers, String authenticatedSensitiveWorkerCode) {

    log.info("sensitiveWorkerCodes: {}", sensitiveWorkerCodes);
    if (sensitiveWorkerCodes.contains(authenticatedSensitiveWorkerCode)) {
      var toExculde =
          sensitiveWorkerCodes.stream()
              .filter(workerCode -> !workerCode.equals(authenticatedSensitiveWorkerCode))
              .toList();
      return workers.stream().filter(worker -> !toExculde.contains(worker.code())).toList();
    }
    return workers.stream()
        .filter(worker -> !sensitiveWorkerCodes.contains(worker.code()))
        .toList();
  }

  public List<DailyExecution> filterMissionExecutionsWithoutSensitiveWorkers(
      List<DailyExecution> dailyExecutions, String authenticatedSensitiveWorkerCode) {

    if (sensitiveWorkerCodes.contains(authenticatedSensitiveWorkerCode)) {
      var toExclude =
          sensitiveWorkerCodes.stream()
              .filter(workerCode -> !workerCode.equals(authenticatedSensitiveWorkerCode))
              .toList();
      return dailyExecutions.stream()
          .filter(dailyExecution -> !toExclude.contains(dailyExecution.worker().code()))
          .toList();
    }
    return dailyExecutions.stream()
        .filter(dailyExecution -> !sensitiveWorkerCodes.contains(dailyExecution.worker().code()))
        .toList();
  }
}
