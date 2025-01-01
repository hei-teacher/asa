package school.hei.asa.endpoint.rest.controller;

import static java.util.Comparator.comparing;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.MissionRepository;

@Controller
@AllArgsConstructor
public class DailyExecutionController {
  private final ThDailyExecutionMapper thDailyExecutionMapper;
  private final MissionExecutionRepository missionExecutionRepository;
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
  public String createDailyExecution(ThDailyExecutionForm dmeForm, Authentication authentication) {
    var worker = workerFromAuthentication.apply(authentication).get();
    missionExecutionRepository.save(thDailyExecutionMapper.toDomain(dmeForm, worker));
    return "redirect:/calendar";
  }
}
