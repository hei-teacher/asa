package school.hei.asa.service;

import static java.time.ZoneId.systemDefault;
import static java.util.Locale.FRENCH;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

@Slf4j
@Service
public class ContractService {
  private final WorkerRepository workerRepository;
  private final ContractRepository contractRepository;
  private final DailyExecutionRepository dailyExecutionRepository;
  private final MissionExecutionRepository missionExecutionRepository;
  private final MissionService missionService;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final DateTimeFormatter localDateFormatter =
      DateTimeFormatter.ofPattern("dd MMM yyyy", FRENCH);

  public ContractService(
      WorkerRepository workerRepository,
      ContractRepository contractRepository,
      DailyExecutionRepository dailyExecutionRepository,
      MissionExecutionRepository missionExecutionRepository,
      MissionService missionService,
      CareProductCodeSupplier careProductCodeSupplier) {
    this.workerRepository = workerRepository;
    this.contractRepository = contractRepository;
    this.dailyExecutionRepository = dailyExecutionRepository;
    this.missionExecutionRepository = missionExecutionRepository;
    this.missionService = missionService;
    this.careProductCodeSupplier = careProductCodeSupplier;
  }

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
    var result =
        executions.stream()
            .map(
                dailyExecution -> {
                  var type = dailyExecution.type(careProductCodeSupplier.get());
                  if (type.equals(fullWork)) {
                    return 1.0d;
                  } else if (type.equals(fullCare)) {
                    return 0.0d;
                  }
                  return dailyExecution.executions().stream()
                      .map(
                          me -> {
                            return missionService.isUnpaidCare(me) ? 0.0d : me.dayPercentage();
                          })
                      .reduce(Double::sum)
                      .get();
                })
            .reduce(Double::sum)
            .get();
    return String.format(java.util.Locale.US, "%.1f", result);
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

    var durationDays = Math.toIntExact(activeContract.duration().toDays());
    if (durationDays <= 0) {
      return true;
    }

    var startDate = activeContract.entranceInstant().atZone(systemDefault()).toLocalDate();
    var now = LocalDate.now();
    var workedStr = getActualWorkedDaysByDateByWorker(startDate, worker.code(), now);
    var workDays = workedStr.equals("-") ? 0.0 : Double.parseDouble(workedStr);

    return workDays < durationDays;
  }

  public long getRemainingDaysByWorker(Worker worker) {
    var contracts = contractRepository.findAllByWorker(worker);
    var activeContract =
        contracts.stream().filter(c -> c.endInstant() == null).findFirst().orElse(null);

    if (activeContract == null) {
      return -1;
    }

    var durationDays = activeContract.duration().toDays();
    if (durationDays <= 0) {
      return Long.MAX_VALUE;
    }

    var startDate = activeContract.entranceInstant().atZone(systemDefault()).toLocalDate();
    var now = LocalDate.now();
    var workedStr = getActualWorkedDaysByDateByWorker(startDate, worker.code(), now);
    var usedDays = workedStr.equals("-") ? 0.0 : Double.parseDouble(workedStr);
    return (long) (durationDays - usedDays);
  }
}
