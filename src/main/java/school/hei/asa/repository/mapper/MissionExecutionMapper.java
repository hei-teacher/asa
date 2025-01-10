package school.hei.asa.repository.mapper;

import static java.util.UUID.randomUUID;

import java.sql.Date;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
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
    jme.setComment(me.comment());
    return jme;
  }

  /*package-private*/ MissionExecution toDomain(JMissionExecution jme, Cache cache) {
    var jWorker = jme.getWorker();
    return new MissionExecution(
        missionMapper.toDomain(jme.getMission(), cache),
        cache.getOrDefault(Worker.class, jWorker.getCode(), workerMapper.toDomain(jWorker, cache)),
        jme.getDate().toLocalDate(),
        jme.getDayPercentage(),
        jme.getComment());
  }

  public MissionExecution toDomain(JMissionExecution jme) {
    return toDomain(jme, new Cache());
  }
}
