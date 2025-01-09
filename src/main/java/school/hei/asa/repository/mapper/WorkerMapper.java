package school.hei.asa.repository.mapper;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.FullTimeEmployee;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.StudentContractor;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JMissionExecution;
import school.hei.asa.repository.model.JWorker;
import school.hei.asa.repository.model.WorkerType;

@AllArgsConstructor
@Component
public class WorkerMapper {

  private final MissionExecutionMapper missionExecutionMapper;

  public Worker toDomain(JWorker jWorker) {
    return toDomain(jWorker, new Cache());
  }

  /*package-private*/ Worker toDomain(JWorker jWorker, Cache cache) {
    var code = jWorker.getCode();
    if (cache.contains(Worker.class, code)) {
      return cache.get(Worker.class, code);
    }

    var worker =
        switch (jWorker.getWorkerType()) {
          case partnerContractor ->
              new PartnerContractor(jWorker.getCode(), jWorker.getName(), jWorker.getEmail());
          case studentContractor ->
              new StudentContractor(jWorker.getCode(), jWorker.getName(), jWorker.getEmail());
          case fullTimeEmployee ->
              new FullTimeEmployee(jWorker.getCode(), jWorker.getName(), jWorker.getEmail());
        };
    cache.put(code, worker);

    executeMissions(jWorker.getMissionExecutions(), worker, cache);
    return worker;
  }

  private void executeMissions(List<JMissionExecution> jmeList, Worker worker, Cache cache) {
    var executionsByDate = jmeList.stream().collect(groupingBy(JMissionExecution::getDate));
    executionsByDate.forEach(
        (date, jmeListByDate) ->
            worker.execute(
                new DailyExecution(
                    worker,
                    date.toLocalDate(),
                    jmeListByDate.stream()
                        .map(jme -> missionExecutionMapper.toDomain(jme, cache))
                        .toList())));
  }

  public JWorker toEntity(Worker worker, List<JMissionExecution> jMissionExecutions) {
    var jWorker = new JWorker();
    jWorker.setCode(worker.code());
    jWorker.setName(worker.name());
    jWorker.setWorkerType(WorkerType.type(worker));
    jWorker.setMissionExecutions(jMissionExecutions);
    return jWorker;
  }
}
