package school.hei.asa.endpoint.rest.controller.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.model.contract.Contract;

@Component
@AllArgsConstructor
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
}
