package school.hei.asa.service;

import static java.util.Locale.US;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.WorkerRepository;

@Service
public class ContractService {
  private final WorkerRepository workerRepository;
  private final ContractRepository contractRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final MissionService missionService;
  private final int lowRemainingDaysThreshold;

  public ContractService(
      WorkerRepository workerRepository,
      ContractRepository contractRepository,
      CareProductCodeSupplier careProductCodeSupplier,
      MissionService missionService,
      @Value("${LOW_CONTRACT_DAYS_THRESOLD}") int lowRemainingDaysThreshold) {
    this.workerRepository = workerRepository;
    this.contractRepository = contractRepository;
    this.careProductCodeSupplier = careProductCodeSupplier;
    this.missionService = missionService;
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

  String executedDays(List<DailyExecution> executions) {
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
                      .map(me -> missionService.isUnpaidCare(me) ? 0.0d : me.dayPercentage())
                      .reduce(0.0d, Double::sum);
                })
            .reduce(0.0d, Double::sum);
    return String.format(US, "%.1f", result);
  }
}
