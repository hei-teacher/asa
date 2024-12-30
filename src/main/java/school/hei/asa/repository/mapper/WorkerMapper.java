package school.hei.asa.repository.mapper;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summingDouble;
import static school.hei.asa.repository.model.JWorker.WorkerType.fullTimeEmployee;
import static school.hei.asa.repository.model.JWorker.WorkerType.partnerContractor;
import static school.hei.asa.repository.model.JWorker.WorkerType.studentContractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.DailyMissionExecution;
import school.hei.asa.model.FullTimeEmployee;
import school.hei.asa.model.Mission;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.StudentContractor;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JMission;
import school.hei.asa.repository.model.JMissionExecution;
import school.hei.asa.repository.model.JWorker;
import school.hei.asa.repository.model.JWorker.WorkerType;

@AllArgsConstructor
@Component
public class WorkerMapper {

  private final MissionMapper missionMapper;

  public Worker toDomain(JWorker jWorker) {
    var worker =
        switch (jWorker.getWorkerType()) {
          case partnerContractor -> new PartnerContractor(jWorker.getCode(), jWorker.getName());
          case studentContractor -> new StudentContractor(jWorker.getCode(), jWorker.getName());
          case fullTimeEmployee -> new FullTimeEmployee(jWorker.getCode(), jWorker.getName());
        };

    var executionsByDate =
        jWorker.getMissionExecutions().stream().collect(groupingBy(JMissionExecution::getDate));
    executionsByDate.forEach(
        (date, meList) ->
            worker.execute(
                new DailyMissionExecution(
                    worker, date.toLocalDate(), toMissionMap(toJMissionMap(meList)))));

    return worker;
  }

  private Map<Mission, Double> toMissionMap(Map<JMission, Double> jMissionMap) {
    Map<Mission, Double> res = new HashMap<>();
    jMissionMap.forEach(
        (jMission, percentage) -> res.put(missionMapper.toDomain(jMission), percentage));
    return res;
  }

  private Map<JMission, Double> toJMissionMap(List<JMissionExecution> jmeList) {
    return jmeList.stream()
        .collect(
            groupingBy(
                JMissionExecution::getMission,
                mapping(
                    JMissionExecution::getExecution_percentage,
                    summingDouble(Double::doubleValue))));
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
