package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.ThMissionService;
import school.hei.asa.service.DailyExecutionService;
import school.hei.asa.service.LowRemainingDaysAlertService;

@Controller
@AllArgsConstructor
public class DailyExecutionController {
  private final ThDailyExecutionFormMapper thDailyExecutionFormMapper;
  private final DailyExecutionService dailyExecutionService;
  private final WorkerFromAuthentication workerFromAuthentication;
  private final ThMissionService thMissionService;
  private final LowRemainingDaysAlertService lowRemainingDaysAlertService;

  @GetMapping("/daily-execution")
  public String getDailyExecutionForm(Model model, Authentication authentication) {
    var worker = workerFromAuthentication.apply(authentication).get();
    var sortedMissions = thMissionService.sortedMissionsWithoutMissionExecution();
    model.addAttribute("missions", sortedMissions);

    var warningBannerMessage =
        lowRemainingDaysAlertService.verifyRemainingDaysAndBuildAlertMessage(worker).orElse(null);
    model.addAttribute("warningBannerMessage", warningBannerMessage);

    return "daily-execution";
  }

  @PostMapping("/daily-execution")
  public String createDailyExecution(Authentication authentication, ThDailyExecutionForm dmeForm) {
    var worker = workerFromAuthentication.apply(authentication).get();
    var dailyExecution = thDailyExecutionFormMapper.toDomain(dmeForm, worker);

    dailyExecutionService.saveAndAlert(dailyExecution);

    return "redirect:/work-and-care-calendar";
  }
}
