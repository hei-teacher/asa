package school.hei.asa.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.WorkerRepository;

@Service
public class ContractService {
  private final WorkerRepository workerRepository;
  private final ContractRepository contractRepository;
  private final int lowRemainingDaysThreshold;

  public ContractService(
      WorkerRepository workerRepository,
      ContractRepository contractRepository,
      @Value("${LOW_CONTRACT_DAYS_THRESOLD}") int lowRemainingDaysThreshold) {
    this.workerRepository = workerRepository;
    this.contractRepository = contractRepository;
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
}
