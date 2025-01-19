package school.hei.asa.repository.mapper;

import static java.util.UUID.randomUUID;

import java.sql.Date;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JMission;
import school.hei.asa.repository.model.JMissionExecution;
import school.hei.asa.repository.model.JWorker;

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
    var jWorkerCode = jme.getWorker_code();
    var jMissionCode = jme.getMission_code();
    return new MissionExecution(
        missionMapper.toDomain(
            cache.getOrDefault(JMission.class, jMissionCode, jme.getMission()), cache),
        cache.getOrDefault(
            Worker.class,
            jWorkerCode,
            workerMapper.toDomain(
                cache.getOrDefault(JWorker.class, jWorkerCode, jme.getWorker()), cache)),
        jme.getDate().toLocalDate(),
        jme.getDayPercentage(),
        jme.getComment());
  }

  public List<MissionExecution> toDomain(
      List<JMissionExecution> jmeList, List<JWorker> jWorkers, List<JMission> jMissions) {
    var cache = new Cache();
    jWorkers.forEach(jWorker -> cache.put(jWorker.getCode(), JWorker.class));
    jMissions.forEach(jMission -> cache.put(jMission.getCode(), JMission.class));

    return toDomain(jmeList, cache);
  }

  public List<MissionExecution> toDomain(List<JMissionExecution> jmeList) {
    return jmeList.stream().map(jme -> toDomain(jme, new Cache())).toList();
  }

  /*package-private*/ List<MissionExecution> toDomain(
      List<JMissionExecution> jmeList, Cache cache) {
    return jmeList.stream().map(jme -> toDomain(jme, cache)).toList();
  }
}
