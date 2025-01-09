package school.hei.asa.repository.mapper;

import static java.util.stream.Collectors.groupingBy;
import static school.hei.asa.repository.model.JWorker.WorkerType.fullTimeEmployee;
import static school.hei.asa.repository.model.JWorker.WorkerType.partnerContractor;
import static school.hei.asa.repository.model.JWorker.WorkerType.studentContractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.FullTimeEmployee;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.StudentContractor;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JMissionExecution;
import school.hei.asa.repository.model.JWorker;
import school.hei.asa.repository.model.JWorker.WorkerType;

@AllArgsConstructor
@Component
public class WorkerMapper {

  private final MissionExecutionMapper missionExecutionMapper;

  public Worker toDomain(JWorker jWorker) {
    return toDomain(jWorker, new HashMap<>());
  }

  /*package-private*/ Worker toDomain(JWorker jWorker, Map<String, Worker> workersByCode) {
    // note(circular-worker-mission-avoidance)
    var code = jWorker.getCode();
    if (workersByCode.containsKey(code)) {
      return workersByCode.get(code);
    }

    var worker =
        switch (jWorker.getWorkerType()) {
          case partnerContractor -> new PartnerContractor(jWorker.getCode(), jWorker.getName());
          case studentContractor -> new StudentContractor(jWorker.getCode(), jWorker.getName());
          case fullTimeEmployee -> new FullTimeEmployee(jWorker.getCode(), jWorker.getName());
        };
    workersByCode.put(code, worker);

    var executionsByDate =
        jWorker.getMissionExecutions().stream().collect(groupingBy(JMissionExecution::getDate));
    executionsByDate.forEach(
        (date, jmeList) ->
            worker.execute(
                new DailyExecution(
                    worker,
                    date.toLocalDate(),
                    jmeList.stream()
                        .map(jme -> missionExecutionMapper.toDomain(jme, workersByCode))
                        .toList())));

    return worker;
  }

  public JWorker toEntity(Worker worker, List<JMissionExecution> jMissionExecutions) {
    var jWorker = new JWorker();
    jWorker.setCode(worker.code());
    jWorker.setName(worker.name());
    jWorker.setWorkerType(type(worker));
    jWorker.setMissionExecutions(jMissionExecutions);
    return jWorker;
  }

  private WorkerType type(Worker worker) {
    if (worker instanceof PartnerContractor) {
      return partnerContractor;
    }
    if (worker instanceof StudentContractor) {
      return studentContractor;
    }
    if (worker instanceof FullTimeEmployee) {
      return fullTimeEmployee;
    }
    throw new IllegalArgumentException("Unsupported worker type");
  }
}
