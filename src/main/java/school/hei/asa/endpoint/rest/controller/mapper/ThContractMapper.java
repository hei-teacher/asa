package school.hei.asa.endpoint.rest.controller.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;

@AllArgsConstructor
@Component
public class ThContractMapper {
  private final ThWorkerMapper thWorkerMapper;

  public List<ThContract> toTh(List<Contract> contracts) {
    List<ThContract> result = new ArrayList<>();

    contracts.forEach(
        current -> {
          var contractLevel = current.level();
          var contractType = thWorkerMapper.toWorkerType(contractLevel.type().name());
          var executedDays =
              current.executions().isEmpty() ? "-" : current.executions().size() + "";
          var compensation =
              switch (contractLevel.type()) {
                case partnerContractor, studentContractor -> contractLevel.dailyPay();
                case fullTimeEmployee -> contractLevel.monthlyPay();
              };
          result.add(
              new ThContract(
                  contractLevel.code(),
                  current.entranceInstant(),
                  contractType,
                  executedDays,
                  BigDecimal.valueOf(compensation),
                  current.jobTitle(),
                  current.duration() == null ? "-" : current.duration().toDays() + "",
                  current.contractBucketKey()));
        });

    return result;
  }

    public Map<Worker, List<ThContract>> toThContractsByWorker(
      Map<Worker, List<Contract>> contractsByWorker) {
    Map<Worker, List<ThContract>> result = new HashMap<>();
    contractsByWorker.forEach(
        (worker, contracts) -> {
          var thContracts = toTh(contracts);
          result.put(worker, thContracts);
        });
    return result;
  }
}
