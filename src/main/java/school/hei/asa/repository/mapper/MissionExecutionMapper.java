package school.hei.asa.repository.mapper;

import static java.util.UUID.randomUUID;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JMissionExecution;

@Component
public class MissionExecutionMapper {

  private final MissionMapper missionMapper;
  private final WorkerMapper workerMapper;

  public MissionExecutionMapper(MissionMapper missionMapper, @Lazy WorkerMapper workerMapper) {
    this.missionMapper = missionMapper;
    this.workerMapper = workerMapper;
  }

  public JMissionExecution toEntity(MissionExecution me) {
    var jme = new JMissionExecution();
    jme.setId(randomUUID().toString());
    jme.setMission(missionMapper.toEntity(me.mission()));
    jme.setWorker(workerMapper.toEntity(me.worker(), List.of(jme)));
    jme.setDate(Date.valueOf(me.date()));
    jme.setDayPercentage(me.dayPercentage());
    return jme;
  }

  /*package-private*/ MissionExecution toDomain(
      JMissionExecution jme,
      // note(circular-missionExecution-worker-avoidance)
      Map<String, Worker> workersByCode,
      // note(circular-missionExecution-mission-avoidance)
      Map<String, Mission> missionsByCode) {
    return new MissionExecution(
        missionMapper.toDomain(jme.getMission(), workersByCode, missionsByCode),
        workersByCode.get(jme.getWorker().getCode()),
        jme.getDate().toLocalDate(),
        jme.getDayPercentage());
  }
}
