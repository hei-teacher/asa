package school.hei.asa.repository.mapper;

import static java.time.ZoneId.systemDefault;

import java.time.Duration;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.model.JContract;
import school.hei.asa.repository.model.JWorker;

@AllArgsConstructor
@Component
public class ContractMapper {

  private final WorkerMapper workerMapper;
  private final ContractLevelMapper contractLevelMapper;

  private final DailyExecutionRepository dailyExecutionRepository;

  public List<Contract> toDomain(List<JContract> jwlhList) {
    return jwlhList.stream().map(jContract -> toDomain(jContract, new Cache())).toList();
  }

  /*package-private*/ Contract toDomain(JContract jContract, Cache cache) {
    var entranceInstant = jContract.getEntranceInstant();
    var startDate = entranceInstant.atZone(systemDefault()).toLocalDate();
    return new Contract(
        workerMapper.toDomain(
            cache.getOrDefault(
                JWorker.class, jContract.getWorker().getCode(), jContract.getWorker()),
            cache),
        jContract.getJobTitle(),
        contractLevelMapper.toDomain(jContract),
        entranceInstant,
        jContract.getEndInstant(),
        Duration.ofDays(jContract.getDurationInDays()),
        jContract.getCompany(),
        jContract.getContractBucketKey());
  }
}
