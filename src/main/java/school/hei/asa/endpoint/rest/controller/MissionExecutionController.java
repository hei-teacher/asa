package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.hei.asa.endpoint.rest.controller.mapper.MissionExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.DailyMissionExecutionForm;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionExecutionRepository;

@Controller
@AllArgsConstructor
public class MissionExecutionController {
  private final MissionExecutionMapper missionExecutionMapper;
  private final MissionExecutionRepository missionExecutionRepository;

  @GetMapping("/daily-mission-execution")
  public String getDailyMissionExecutionForm() {
    return "daily-mission-execution";
  }

  @PostMapping("/daily-mission-execution")
  public void createDailyMissionExecution(DailyMissionExecutionForm dmeForm, Worker worker) {
    missionExecutionRepository.save(missionExecutionMapper.toDomain(dmeForm, worker));
  }
}
