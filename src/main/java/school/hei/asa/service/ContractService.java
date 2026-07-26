package school.hei.asa.service;

import static java.time.ZoneId.systemDefault;
import static java.util.Locale.FRENCH;
import static java.util.Locale.US;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  public Optional<Contract> findActiveContractByWorker(Worker worker) {
    return contractRepository.findActiveContractByWorker(worker);
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
    return String.format(US, "%.1f", result);
  }

  public List<Contract> findActiveContracts() {
    return contractRepository.findAllActiveContracts();
  }
}
