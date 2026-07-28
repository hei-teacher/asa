package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.ThMissionService;
import school.hei.asa.service.DailyExecutionService;

@Controller
@AllArgsConstructor
public class DailyExecutionController {
  private final ThDailyExecutionFormMapper thDailyExecutionFormMapper;
  private final DailyExecutionService dailyExecutionService;
  private final WorkerFromAuthentication workerFromAuthentication;
  private final ThMissionService thMissionService;

  @GetMapping("/daily-execution")
  public String getDailyExecutionForm(Model model) {
    var sortedMissions = thMissionService.sortedMissionsWithoutMissionExecution();
    model.addAttribute("missions", sortedMissions);
    return "daily-execution";
  }

  @PostMapping("/daily-execution")
  public String createDailyExecution(
      Authentication authentication,
      ThDailyExecutionForm dmeForm,
      RedirectAttributes redirectAttributes) {
    var worker = workerFromAuthentication.apply(authentication).get();
    var dailyExecution = thDailyExecutionFormMapper.toDomain(dmeForm, worker);

    dailyExecutionService.saveAndAlert(dailyExecution);

    return "redirect:/work-and-care-calendar";
  }
}
