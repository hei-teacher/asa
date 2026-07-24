package school.hei.asa.service;

import static java.time.ZoneId.systemDefault;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.LowRemainingDaysAlertRequested;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.number.DaysFormatter;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

@Slf4j
@Service
public class ContractService {
  private final WorkerRepository workerRepository;
  private final ContractRepository contractRepository;
  private final DailyExecutionRepository dailyExecutionRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final MissionService missionService;
  private final EventProducer<LowRemainingDaysAlertRequested> eventProducer;
  private final int lowRemainingDaysThreshold;

  public ContractService(
      WorkerRepository workerRepository,
      ContractRepository contractRepository,
      DailyExecutionRepository dailyExecutionRepository,
      CareProductCodeSupplier careProductCodeSupplier,
      MissionService missionService,
      EventProducer<LowRemainingDaysAlertRequested> eventProducer,
      @Value("${LOW_CONTRACT_DAYS_THRESOLD}") int lowRemainingDaysThreshold) {
    this.workerRepository = workerRepository;
    this.contractRepository = contractRepository;
    this.dailyExecutionRepository = dailyExecutionRepository;
    this.careProductCodeSupplier = careProductCodeSupplier;
    this.missionService = missionService;
    this.eventProducer = eventProducer;
    this.lowRemainingDaysThreshold = lowRemainingDaysThreshold;
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

  public Contract getActiveContractOrThrow(Worker worker) {
    return findActiveContract(worker)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "You do not have an active contract. Please contact your administrator."));
  }

  public Optional<Contract> findActiveContract(Worker worker) {
    return getAllContractsByWorker(worker).stream().filter(c -> c.duration() != null).findFirst();
  }

  public List<Contract> findActiveContracts() {
    return contractRepository.findAllActiveContracts();
  }

  public boolean isBelowThreshold(double remainingDays) {
    return remainingDays < lowRemainingDaysThreshold;
  }

  public double getRemainingDaysOnActiveContractOrZero(Worker worker) {
    var activeContractOpt = findActiveContract(worker);
    if (activeContractOpt.isEmpty()) {
      return 0d;
    }

    var contract = activeContractOpt.get();
    var startDate = contract.entranceInstant().atZone(systemDefault()).toLocalDate();
    var endDate =
        contract.endInstant() == null
            ? LocalDate.now()
            : contract.endInstant().atZone(systemDefault()).toLocalDate();
    var dailyExecutions =
        dailyExecutionRepository.findByWorkerCodeAndDateBetween(worker.code(), startDate, endDate);
    var workedDays = executedDays(dailyExecutions);
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

  public Optional<String> checkRemainingDaysAndBuildAlertMessage(Worker worker) {
    var remainingDays = getRemainingDaysOnActiveContractOrZero(worker);

    if (remainingDays == 0 || !isBelowThreshold(remainingDays)) {
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
