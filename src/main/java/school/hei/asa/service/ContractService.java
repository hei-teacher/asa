package school.hei.asa.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.ContractRepository;

@Service
@AllArgsConstructor
public class ContractService {
  private final ContractRepository contractRepository;

  public List<Contract> getAllContractsForWorker(Worker worker) {
    return contractRepository.findAllByWorker(worker);
  }
}
