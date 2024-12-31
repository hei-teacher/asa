package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.repository.jrepository.JMissionExecutionRepository;
import school.hei.asa.repository.mapper.MissionExecutionMapper;
import school.hei.asa.repository.model.JMissionExecution;

@AllArgsConstructor
@Component
public class MissionExecutionRepository {
  private final JMissionExecutionRepository jMissionExecutionRepository;
  private final MissionExecutionMapper missionExecutionMapper;

  @Transactional
  public void save(DailyExecution dailyExecution) {
    var toSave = new ArrayList<JMissionExecution>();
    dailyExecution
        .executions()
        .forEach(missionExecution -> toSave.add(missionExecutionMapper.toEntity(missionExecution)));
    jMissionExecutionRepository.saveAll(toSave);
  }
}
