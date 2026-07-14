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
  private final MissionService missionService;
  private CareProductCodeSupplier careProductCodeSupplier;
  private final LowRemainingDaysAlertService lowRemainingDaysAlertService;
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

  public Double getRemainingDaysByWorker(Worker worker) {
    var contracts = contractRepository.findAllByWorker(worker);

    if (contracts.isEmpty()) {
      return null;
    }

    var activeContractOpt = contracts.stream().filter(c -> c.duration() != null).findFirst();

    if (activeContractOpt.isEmpty()) {
      return null;
    }

    var contract = activeContractOpt.get();
    var startDate = contract.entranceInstant().atZone(systemDefault()).toLocalDate();
    var endDate =
        contract.endInstant() == null
            ? LocalDate.now()
            : contract.endInstant().atZone(systemDefault()).toLocalDate();
    var actualWorkedDays = getActualWorkedDaysByDateByWorker(startDate, worker.code(), endDate);
    var workedDays = actualWorkedDays.equals("-") ? 0d : Double.parseDouble(actualWorkedDays);
    var remainingDays = contract.duration().toDays() - workedDays;

    return remainingDays;
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

  public Optional<String> checkRemainingDaysAndBuildAlertMessage(Worker worker) {
    var activeContract = getActiveContractOrThrow(worker);
    var remainingDays = getRemainingDaysByWorker(worker);

    if (remainingDays != null && remainingDays <= 0) {
      throw new IllegalStateException(
          "You have no more days available under your contract. Please contact your"
              + " administrator.");
    }

    boolean alertSent =
        remainingDays != null
            && lowRemainingDaysAlertService.checkAndAlert(
                worker, activeContract, remainingDays.longValue());

    return alertSent
        ? Optional.of(
            "Please note : You have "
                + remainingDays.longValue()
                + " day(s) left on your contract !")
        : Optional.empty();
  }

  private Contract getActiveContractOrThrow(Worker worker) {
    return getAllContractsByWorker(worker).stream()
        .filter(c -> c.duration() != null)
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "You do not have an active contract. Please contact your administrator."));
  }

  public boolean isRemainingDaysLow(Double remainingDays) {
    return remainingDays != null
        && lowRemainingDaysAlertService.isBelowThreshold(remainingDays.longValue());
  }

  public List<Contract> findActiveContracts() {
    return contractRepository.findAllActiveContracts();
  }
}
