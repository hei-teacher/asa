package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.hei.asa.endpoint.rest.controller.mapper.ThMissionExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyMissionExecutionForm;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionExecutionRepository;

@Controller
@AllArgsConstructor
public class MissionExecutionController {
  private final ThMissionExecutionMapper thMissionExecutionMapper;
  private final MissionExecutionRepository missionExecutionRepository;

  @GetMapping("/daily-mission-execution")
  public String getDailyMissionExecutionForm() {
    return "daily-mission-execution";
  }

  @PostMapping("/daily-mission-execution")
  public void createDailyMissionExecution(ThDailyMissionExecutionForm dmeForm, Worker worker) {
    missionExecutionRepository.save(thMissionExecutionMapper.toDomain(dmeForm, worker));
  }
}
