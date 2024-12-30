package school.hei.asa.repository.mapper;

import static java.util.UUID.randomUUID;

import java.sql.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.DailyMissionExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.repository.model.JMissionExecution;

@AllArgsConstructor
@Component
public class MissionExecutionMapper {

  private final MissionMapper missionMapper;
  private final WorkerMapper workerMapper;

  public JMissionExecution toEntity(DailyMissionExecution dme, Mission mission, Double percentage) {
    var jme = new JMissionExecution();
    jme.setId(randomUUID().toString());
    jme.setMission(missionMapper.toEntity(mission));
    jme.setWorker(workerMapper.toEntity(dme.worker(), List.of(jme)));
    jme.setDate(Date.valueOf(dme.date()));
    jme.setExecution_percentage(percentage);
    return jme;
  }
}
