package school.hei.asa.repository.mapper;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import school.hei.asa.model.Mission;
import school.hei.asa.repository.model.JMission;

@Component
public class MissionMapper {

  private final ProductMapper productMapper;

  public MissionMapper(@Lazy ProductMapper productMapper) {
    this.productMapper = productMapper;
  }

  public Mission toDomain(JMission jMission) {
    return new Mission(
        jMission.getCode(),
        jMission.getTitle(),
        jMission.getDescription(),
        jMission.getMaxDurationInDays(),
        productMapper.toDomain(jMission.getProduct()));
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
