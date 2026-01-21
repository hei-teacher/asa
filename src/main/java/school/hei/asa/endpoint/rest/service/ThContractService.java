package school.hei.asa.endpoint.rest.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThContractMapper;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.model.Worker;
import school.hei.asa.service.ContractService;

@Service
@AllArgsConstructor
public class ThContractService {
  private final ThContractMapper thContractMapper;
  private final ContractService contractService;

  public List<ThContract> getAllContractsForWorker(Worker worker) {
    var contracts = contractService.getAllContractsForWorker(worker);
    return thContractMapper.toTh(contracts);
  }
}
