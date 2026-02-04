package school.hei.asa.endpoint.rest.controller.mapper;

import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.service.MissionService;

@AllArgsConstructor
@Component
public class ThContractMapper {
  private final ThWorkerMapper thWorkerMapper;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final MissionService missionService;

  public List<ThContract> toTh(List<Contract> contracts) {
    List<ThContract> result = new ArrayList<>();

    contracts.forEach(
        current -> {
          var contractLevel = current.level();
          var contractType = thWorkerMapper.toWorkerType(contractLevel.type().name());
          var executedDays = executedDays(current.executions());
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

  private String executedDays(List<DailyExecution> executions) {
    if (executions.isEmpty()) {
      return "-";
    }
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
                  .map(
                      me -> {
                        return missionService.isUnpaidCare(me) ? 0.0d : me.dayPercentage();
                      })
                  .reduce(Double::sum)
                  .get();
            })
        .reduce(Double::sum)
        .get()
        .toString();
  }
}
