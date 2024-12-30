package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.asa.model.DailyMissionExecution;
import school.hei.asa.repository.MissionExecutionRepository;

@RestController
@AllArgsConstructor
public class MissionExecutionController {
  private final MissionExecutionRepository missionExecutionRepository;

  @PostMapping("/mission-execution")
  public void createMissionExecution(DailyMissionExecution dme) {
    missionExecutionRepository.save(dme);
  }
}
