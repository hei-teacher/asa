package school.hei.asa.repository.mapper;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JMission;
import school.hei.asa.repository.model.JProduct;

@Component
public class MissionMapper {

  private final ProductMapper productMapper;
  private final MissionExecutionMapper missionExecutionMapper;

  public MissionMapper(
      @Lazy ProductMapper productMapper, @Lazy MissionExecutionMapper missionExecutionMapper) {
    this.productMapper = productMapper;
    this.missionExecutionMapper = missionExecutionMapper;
  }

  public Mission toDomain(JMission jMission) {
    return toDomain(jMission, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  /*package-private*/ Mission toDomain(
      JMission jMission,
      // note(circular-missionExecution-worker-avoidance)
      Map<String, Worker> workersByCode,
      // note(circular-missionExecution-mission-avoidance)
      Map<String, Mission> missionsByCode,
      // note(circular-mission-product-avoidance)
      Map<String, Product> productsByCode) {
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
            productsByCode.getOrDefault(
                jMission.getProduct().getCode(),
                productMapper.toDomain(jMission.getProduct(), productsByCode)));
    missionsByCode.put(missionCode, mission);

    jMission.getMissionExecutions().stream()
        .map(
            jme ->
                missionExecutionMapper.toDomain(jme, workersByCode, missionsByCode, productsByCode))
        .forEach(mission::add);
    return mission;
  }

  public JMission toEntity(Mission mission) {
    return toEntity(mission, new HashMap<>());
  }

  /*package-private*/ JMission toEntity(
      Mission mission,
      // note(circular-jmission-jproduct-avoidance)
      Map<String, JProduct> jProductsByCode) {
    var jMission = new JMission();
    jMission.setCode(mission.code());
    jMission.setTitle(mission.title());
    jMission.setDescription(mission.description());
    jMission.setMaxDurationInDays(mission.maxDurationInDays());
    var missionProduct = mission.product();
    jMission.setProduct(
        jProductsByCode.getOrDefault(
            missionProduct.code(), productMapper.toEntity(missionProduct, jProductsByCode)));
    return jMission;
  }
}
