package school.hei.asa.endpoint.rest.controller;

import static java.time.LocalDate.now;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.DailyExecutionService;
import school.hei.asa.endpoint.rest.service.ThMissionService;
import school.hei.asa.service.CalendarService;
import school.hei.asa.service.ContractService;

@AllArgsConstructor
@Controller
public class DailyExecutionController {
  private final WorkerFromAuthentication workerFromAuthentication;
  private final ThMissionService thMissionService;
  private final ContractService contractService;
  private final DailyExecutionService dailyExecutionService;
  private final CalendarService calendarService;

  @GetMapping("/daily-execution")
  public String getDailyExecutionForm(Authentication authentication, Model model) {
    var sortedMissions = thMissionService.sortedMissionsWithoutMissionExecution();
    model.addAttribute("missions", sortedMissions);

    var worker = workerFromAuthentication.apply(authentication).get();
    calendarService
        .contractAlertMessage(worker, now().getYear())
        .ifPresent(msg -> model.addAttribute("contractAlert", msg));

    return "daily-execution";
  }

  @PostMapping("/daily-execution")
  public String createDailyExecution(
      Authentication authentication,
      ThDailyExecutionForm dmeForm,
      RedirectAttributes redirectAttributes) {
    var worker = workerFromAuthentication.apply(authentication).get();

    var remainingError = contractService.checkRemainingDays(worker);
    if (remainingError.isPresent()) {
      redirectAttributes.addFlashAttribute("error", remainingError.get());
      return "redirect:/daily-execution";
    }

    dailyExecutionService.saveAndAlert(dmeForm, worker);

    return "redirect:/work-and-care-calendar";
  }
}
