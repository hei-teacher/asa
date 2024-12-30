package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.DailyMissionExecution;
import school.hei.asa.repository.jrepository.JMissionExecutionRepository;
import school.hei.asa.repository.mapper.MissionExecutionMapper;
import school.hei.asa.repository.model.JMissionExecution;

@AllArgsConstructor
@Component
public class MissionExecutionRepository {
  private final JMissionExecutionRepository jMissionExecutionRepository;
  private final MissionExecutionMapper missionExecutionMapper;

  @Transactional
  public void save(DailyMissionExecution dme) {
    var toSave = new ArrayList<JMissionExecution>();
    dme.missionPercentages()
        .forEach(
            (mission, percentage) ->
                toSave.add(missionExecutionMapper.toEntity(dme, mission, percentage)));
    jMissionExecutionRepository.saveAll(toSave);
  }
}
