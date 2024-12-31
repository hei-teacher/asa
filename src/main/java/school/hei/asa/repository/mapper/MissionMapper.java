package school.hei.asa.repository.mapper;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JMission;

@Component
public class MissionMapper {

  private final ProductMapper productMapper;
  private final MissionExecutionMapper missionExecutionMapper;

  public MissionMapper(
      ProductMapper productMapper, @Lazy MissionExecutionMapper missionExecutionMapper) {
    this.productMapper = productMapper;
    this.missionExecutionMapper = missionExecutionMapper;
  }

  public Mission toDomain(JMission jMission) {
    return toDomain(jMission, new HashMap<>(), new HashMap<>());
  }

  /*package-private*/ Mission toDomain(
      JMission jMission,
      // note(circular-missionExecution-worker-avoidance)
      Map<String, Worker> workersByCode,
      // note(circular-missionExecution-mission-avoidance)
      Map<String, Mission> missionsByCode) {
    var missionCode = jMission.getCode();
    if (missionsByCode.containsKey(missionCode)) {
      return missionsByCode.get(missionCode);
    }

    var mission =
        new Mission(
            missionCode,
            jMission.getTitle(),
            jMission.getDescription(),
            jMission.getMaxDurationInDays(),
            productMapper.toDomain(jMission.getProduct()));
    missionsByCode.put(missionCode, mission);

    jMission.getMissionExecutions().stream()
        .map(jme -> missionExecutionMapper.toDomain(jme, workersByCode, missionsByCode))
        .forEach(mission::add);
    return mission;
  }

  public JMission toEntity(Mission mission) {
    var jMission = new JMission();
    jMission.setCode(mission.code());
    jMission.setTitle(mission.title());
    jMission.setDescription(mission.description());
    jMission.setMaxDurationInDays(mission.maxDurationInDays());
    jMission.setProduct(productMapper.toEntity(mission.product()));
    return jMission;
  }
}
