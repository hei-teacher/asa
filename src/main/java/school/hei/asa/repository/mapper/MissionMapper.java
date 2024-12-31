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
  private final WorkerMapper workerMapper;

  public MissionMapper(ProductMapper productMapper, @Lazy WorkerMapper workerMapper) {
    this.productMapper = productMapper;
    this.workerMapper = workerMapper;
  }

  public Mission toDomain(JMission jMission) {
    return toDomain(jMission, new HashMap<>());
  }

  /*package-private*/ Mission toDomain(
      JMission jMission,
      // note(circular-worker-mission-avoidance)
      Map<String, Worker> workersByCode) {
    var mission =
        new Mission(
            jMission.getCode(),
            jMission.getTitle(),
            jMission.getDescription(),
            jMission.getMaxDurationInDays(),
            productMapper.toDomain(jMission.getProduct()));
    jMission
        .getMissionExecutions()
        .forEach(jme -> mission.addWorker(workerMapper.toDomain(jme.getWorker(), workersByCode)));
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
