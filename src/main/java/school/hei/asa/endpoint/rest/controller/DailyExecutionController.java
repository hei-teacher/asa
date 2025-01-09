package school.hei.asa.endpoint.rest.controller;

import static java.util.Comparator.comparing;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.MissionRepository;

@Controller
@AllArgsConstructor
public class DailyExecutionController {
  private final ThDailyExecutionFormMapper thDailyExecutionFormMapper;
  private final MissionExecutionRepository missionExecutionRepository;
  private final DailyExecutionRepository dailyExecutionRepository;
  private final MissionRepository missionRepository;
  private final WorkerFromAuthentication workerFromAuthentication;

  @GetMapping("/daily-execution")
  public String getDailyExecutionForm(Model model) {
    var sortedMissions =
        missionRepository.findAll().stream()
            .map(mission -> new ThMission(mission.code(), mission.title()))
            .sorted(comparing(ThMission::getCode))
            .toList();
    model.addAttribute("missions", sortedMissions);
    return "daily-execution";
  }

  @PostMapping("/daily-execution")
  public String createDailyExecution(Authentication authentication, ThDailyExecutionForm dmeForm) {
    var worker = workerFromAuthentication.apply(authentication).get();
    var dailyExecution = thDailyExecutionFormMapper.toDomain(dmeForm, worker);
    var date = dailyExecution.date();
    if (!missionExecutionRepository.findAllBy(worker, date).isEmpty()) {
      throw new IllegalArgumentException("Day already has MissionExecution: " + date);
    }
    dailyExecutionRepository.save(dailyExecution);
    return "redirect:/work-and-care-calendar";
  }
}
