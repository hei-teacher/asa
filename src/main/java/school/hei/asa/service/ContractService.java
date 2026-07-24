package school.hei.asa.service;

import static java.time.ZoneId.systemDefault;
import static java.util.Locale.US;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

@Service
@AllArgsConstructor
public class ContractService {
  private final WorkerRepository workerRepository;
  private final ContractRepository contractRepository;
  private final DailyExecutionRepository dailyExecutionRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final MissionService missionService;

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

  public Contract getActiveContractOrThrow(Worker worker) {
    return findActiveContractByWorker(worker)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "You do not have an active contract. Please contact your administrator."));
  }

  public Optional<Contract> findActiveContractByWorker(Worker worker) {
    return getAllContractsByWorker(worker).stream().filter(c -> c.duration() != null).findFirst();
  }

  public List<Contract> findActiveContracts() {
    return contractRepository.findAllActiveContracts();
  }

  public double getRemainingDaysOnActiveContractOrZero(Worker worker) {
    var activeContractOpt = findActiveContractByWorker(worker);
    if (activeContractOpt.isEmpty()) {
      return 0d;
    }

    var contract = activeContractOpt.get();
    var startDate = contract.entranceInstant().atZone(systemDefault()).toLocalDate();
    var endDate =
        contract.endInstant() == null
            ? LocalDate.now()
            : contract.endInstant().atZone(systemDefault()).toLocalDate();
    var actualWorkedDays = getActualWorkedDaysByDateByWorker(startDate, worker.code(), endDate);
    var workedDays = actualWorkedDays.equals("-") ? 0d : Double.parseDouble(actualWorkedDays);
    return contract.duration().toDays() - workedDays;
  }

  public double executedDays(List<DailyExecution> executions) {
    return executions.stream()
        .map(
            dailyExecution -> {
              var type = dailyExecution.type(careProductCodeSupplier.get());
              if (type.equals(fullWork)) {
                return 1.0d;
              } else if (type.equals(fullCare)) {
                return 0.0d;
              }
              return dailyExecution.executions().stream()
                  .map(me -> missionService.isUnpaidCare(me) ? 0.0d : me.dayPercentage())
                  .reduce(0.0d, Double::sum);
            })
        .reduce(0.0d, Double::sum);
  }

  public String getActualWorkedDaysByDateByWorker(
      LocalDate startDate, String workerCode, LocalDate endDate) {
    var dailyExecutions =
        dailyExecutionRepository.findByWorkerCodeAndDateBetween(workerCode, startDate, endDate);
    if (dailyExecutions.isEmpty()) {
      return "-";
    }
    return String.format(US, "%.1f", executedDays(dailyExecutions));
  }
}
