package school.hei.asa.repository.mapper;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.*;
import school.hei.asa.repository.model.*;

@AllArgsConstructor
@Component
public class WorkerLevelHistoryMapper {

  private final WorkerMapper workerMapper;

  public List<WorkerLevelHistory> toDomain(List<JWorkerLevelHistory> jwlhList) {
    return jwlhList.stream()
        .map(jWorkerLevelHistory -> toDomain(jWorkerLevelHistory, new Cache()))
        .toList();
  }

  /*package-private*/ WorkerLevelHistory toDomain(
      JWorkerLevelHistory jWorkerLevelHistory, Cache cache) {
    var jWorkerCode = jWorkerLevelHistory.getWorker_code();
    return new WorkerLevelHistory(
        workerMapper.toDomain(
            cache.getOrDefault(JWorker.class, jWorkerCode, jWorkerLevelHistory.getWorker()), cache),
        jWorkerLevelHistory.getLevel(),
        jWorkerLevelHistory.getEntranceInstant(),
        jWorkerLevelHistory.getContractType(),
        jWorkerLevelHistory.getTotalWorkDays(),
        jWorkerLevelHistory.getCompensation(),
        jWorkerLevelHistory.getJobTitle(),
        jWorkerLevelHistory.getContractDuration(),
        jWorkerLevelHistory.getContractBucketKey());
  }
}
