package school.hei.asa.repository;

import static java.util.UUID.randomUUID;

import jakarta.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.DailyMissionExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.repository.jrepository.JMissionExecutionRepository;
import school.hei.asa.repository.mapper.MissionMapper;
import school.hei.asa.repository.mapper.WorkerMapper;
import school.hei.asa.repository.model.JMissionExecution;

@AllArgsConstructor
@Component
public class MissionExecutionRepository {
  private final JMissionExecutionRepository jMissionExecutionRepository;
  private final MissionMapper missionMapper;
  private final WorkerMapper workerMapper;

  @Transactional
  public void save(DailyMissionExecution dme) {
    var toSave = new ArrayList<JMissionExecution>();
    dme.missionPercentages()
        .forEach((mission, percentage) -> toSave.add(toEntity(dme, mission, percentage)));
    jMissionExecutionRepository.saveAll(toSave);
  }

  private JMissionExecution toEntity(
      DailyMissionExecution dme, Mission mission, Double percentage) {
    var jme = new JMissionExecution();
    jme.setId(randomUUID().toString());
    jme.setMission(missionMapper.toEntity(mission, List.of(jme)));
    jme.setWorker(workerMapper.toEntity(dme.worker(), List.of(jme)));
    jme.setDate(Date.valueOf(dme.date()));
    jme.setExecution_percentage(percentage);
    return jme;
  }
}
