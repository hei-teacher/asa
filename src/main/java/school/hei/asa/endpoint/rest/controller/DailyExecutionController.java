package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionExecutionRepository;

@Controller
@AllArgsConstructor
public class DailyExecutionController {
  private final ThDailyExecutionMapper thDailyExecutionMapper;
  private final MissionExecutionRepository missionExecutionRepository;

  @GetMapping("/daily-execution")
  public String getDailyExecutionForm() {
    return "daily-execution";
  }

  @PostMapping("/daily-execution")
  public void createDailyExecution(ThDailyExecutionForm dmeForm, Worker worker) {
    missionExecutionRepository.save(thDailyExecutionMapper.toDomain(dmeForm, worker));
  }
}
