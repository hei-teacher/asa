package school.hei.asa.service;

import static java.time.ZoneId.systemDefault;
import static java.util.Locale.FRENCH;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;
import static school.hei.asa.model.contract.ContractType.fullTimeEmployee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

@Slf4j
@Service
@AllArgsConstructor
public class ContractService {
  private final WorkerRepository workerRepository;
  private final ContractRepository contractRepository;
  private final DailyExecutionRepository dailyExecutionRepository;
  private final MissionService missionService;
  private CareProductCodeSupplier careProductCodeSupplier;
  private final DateTimeFormatter localDateFormatter =
      DateTimeFormatter.ofPattern("dd MMM yyyy", FRENCH);

  public Map<Worker, List<Contract>> totalWorkDaysPerWorker() {
    return contractRepository.findAll().stream().collect(Collectors.groupingBy(Contract::worker));
  }

  public Map<Worker, List<Contract>> totalWorkDaysForOneWorker(String workerCode) {
    Map<Worker, List<Contract>> result = new HashMap<>();
    var worker = workerRepository.findByCode(workerCode);
    var contracts = contractRepository.findAllByWorker(worker);
    result.put(worker, contracts);
    return result;
  }

  public List<Contract> getAllContractsByWorker(Worker worker) {
    return contractRepository.findAllByWorker(worker);
  }

  public String getActualWorkedDaysByDateByWorker(
      LocalDate startDate, String workerCode, LocalDate endDate) {
    var dailyExecutions =
        dailyExecutionRepository.findByWorkerCodeAndDateBetween(workerCode, startDate, endDate);
    return executedDays(dailyExecutions);
  }

  private String executedDays(List<DailyExecution> executions) {
    if (executions.isEmpty()) {
      return "-";
    }
    return String.format("%.1f", actualWorkedDays(executions));
  }

  private double actualWorkedDays(List<DailyExecution> executions) {
    return executions.stream()
        .mapToDouble(
            dailyExecution -> {
              var type = dailyExecution.type(careProductCodeSupplier.get());
              if (type.equals(fullWork)) {
                return 1.0d;
              } else if (type.equals(fullCare)) {
                return 0.0d;
              }
              return dailyExecution.executions().stream()
                  .mapToDouble(me -> missionService.isUnpaidCare(me) ? 0.0d : me.dayPercentage())
                  .sum();
            })
        .sum();
  }

  public List<Contract> findActiveContracts() {
    return contractRepository.findAllActiveContracts();
  }

  public boolean hasRemainingDays(Worker worker) {
    var contracts = contractRepository.findAllByWorker(worker);
    var activeContract =
        contracts.stream().filter(c -> c.endInstant() == null).findFirst().orElse(null);

    if (activeContract == null) {
      return false;
    }

    if (activeContract.level().type() == fullTimeEmployee) {
      return true;
    }

    var durationDays = (int) activeContract.duration().toDays();

    var usedDays = usedDays(worker, activeContract);

    return usedDays < durationDays;
  }

  public double remainingDays(Worker worker) {
    var contracts = contractRepository.findAllByWorker(worker);
    var activeContract =
        contracts.stream().filter(c -> c.endInstant() == null).findFirst().orElse(null);

    if (activeContract == null) {
      return -1;
    }

    if (activeContract.level().type() == fullTimeEmployee) {
      return Double.MAX_VALUE;
    }

    var durationDays = activeContract.duration().toDays();

    var usedDays = usedDays(worker, activeContract);
    return durationDays - usedDays;
  }

  private double usedDays(Worker worker, Contract activeContract) {
    var startDate = activeContract.entranceInstant().atZone(systemDefault()).toLocalDate();
    var now = LocalDate.now();
    var dailyExecutions =
        dailyExecutionRepository.findByWorkerCodeAndDateBetween(worker.code(), startDate, now);
    return actualWorkedDays(dailyExecutions);
  }
}
