package school.hei.asa.repository.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.asa.model.Mission;
import school.hei.asa.repository.model.JMission;
import school.hei.asa.repository.model.JMissionExecution;

@Component
public class MissionMapper {
  public Mission toDomain(JMission jMission) {
    return new Mission(
        jMission.getCode(),
        jMission.getTitle(),
        jMission.getDescription(),
        jMission.getMaxDurationInDays());
  }

  public JMission toEntity(Mission mission, List<JMissionExecution> jMissionExecutions) {
    var jMission = new JMission();
    jMission.setCode(mission.code());
    jMission.setTitle(mission.title());
    jMission.setDescription(mission.description());
    jMission.setMaxDurationInDays(mission.maxDurationInDays());
    jMission.setMissionExecutions(jMissionExecutions);
    return jMission;
  }
}
