package school.hei.asa.service;

import static java.util.Comparator.comparing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.WorkerRepository;

@Service
@AllArgsConstructor
public class ContractService {
  private final WorkerRepository workerRepository;
  private final ContractRepository contractRepository;

  public Map<Worker, List<Contract>> totalWorkDaysPerWorker() {
    Map<Worker, List<Contract>> result = new HashMap<>();
    var workers = workerRepository.findAll().stream().sorted(comparing(Worker::name)).toList();
    workers.parallelStream()
        .forEach(
            worker -> {
              var contracts = contractRepository.findAllByWorker(worker);
              result.put(worker, contracts);
            });
    return result;
  }

  public Map<Worker, List<Contract>> totalWorkDaysForOneWorker(String workerCode) {
    Map<Worker, List<Contract>> result = new HashMap<>();
    var worker = workerRepository.findByCode(workerCode);
    var contracts = contractRepository.findAllByWorker(worker);
    result.put(worker, contracts);
    return result;
  }
}
