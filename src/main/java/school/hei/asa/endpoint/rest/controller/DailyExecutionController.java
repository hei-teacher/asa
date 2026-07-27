package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.ThMissionService;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.LowRemainingDaysAlertService;

@Controller
@AllArgsConstructor
public class DailyExecutionController {
  private final ThDailyExecutionFormMapper thDailyExecutionFormMapper;
  private final DailyExecutionRepository dailyExecutionRepository;
  private final WorkerFromAuthentication workerFromAuthentication;
  private final ThMissionService thMissionService;
  private final LowRemainingDaysAlertService lowRemainingDaysAlertService;
  private final ContractService contractService;

  @GetMapping("/daily-execution")
  public String getDailyExecutionForm(Model model) {
    var sortedMissions = thMissionService.sortedMissionsWithoutMissionExecution();
    model.addAttribute("missions", sortedMissions);
    return "daily-execution";
  }

  @PostMapping("/daily-execution")
  public String createDailyExecution(Authentication authentication, ThDailyExecutionForm dmeForm) {
    return createDailyExecution(authentication, dmeForm, new RedirectAttributesModelMap());
  }

  public String createDailyExecution(
      Authentication authentication,
      ThDailyExecutionForm dmeForm,
      RedirectAttributes redirectAttributes) {
    var worker = workerFromAuthentication.apply(authentication).get();

    var remainingDays = contractService.getRemainingDaysOnActiveContractOrZero(worker);
    var hasUsableContract =
        contractService.findActiveContractByWorker(worker).isPresent() && remainingDays > 0;
    if (!hasUsableContract) {
      throw new IllegalStateException("Unable to punch in : you have no active contract.");
    }

    var dailyExecution = thDailyExecutionFormMapper.toDomain(dmeForm, worker);

    dailyExecutionRepository.save(dailyExecution);

    lowRemainingDaysAlertService.sendAlertEmailIfLowRemainingDays(worker);

    lowRemainingDaysAlertService
        .checkRemainingDaysAndBuildAlertMessage(worker)
        .ifPresent(
            message -> {
              redirectAttributes.addFlashAttribute("toastMessage", message);
              redirectAttributes.addFlashAttribute("toastType", "warning");
            });

    return "redirect:/work-and-care-calendar";
  }
}
