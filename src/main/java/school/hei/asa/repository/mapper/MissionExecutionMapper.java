package school.hei.asa.repository.mapper;

import static java.util.UUID.randomUUID;

import java.sql.Date;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.repository.model.JMissionExecution;

@Component
public class MissionExecutionMapper {

  private final MissionMapper missionMapper;
  private final WorkerMapper workerMapper;

  public MissionExecutionMapper(
      @Lazy MissionMapper missionMapper, @Lazy WorkerMapper workerMapper) {
    this.missionMapper = missionMapper;
    this.workerMapper = workerMapper;
  }

  public JMissionExecution toEntity(MissionExecution me) {
    var jme = new JMissionExecution();
    jme.setId(randomUUID().toString());
    jme.setMission(missionMapper.toEntity(me.mission()));
    jme.setWorker(workerMapper.toEntity(me.worker(), List.of(jme)));
    jme.setDate(Date.valueOf(me.date()));
    jme.setExecution_percentage(me.dayPercentage());
    return jme;
  }

  public MissionExecution toDomain(JMissionExecution jMissionExecution) {
    return new MissionExecution(
        missionMapper.toDomain(jMissionExecution.getMission()),
        workerMapper.toDomain(jMissionExecution.getWorker()),
        jMissionExecution.getDate().toLocalDate(),
        jMissionExecution.getExecution_percentage());
  }
}
