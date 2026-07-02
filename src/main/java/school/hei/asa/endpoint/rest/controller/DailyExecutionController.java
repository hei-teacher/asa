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
  private final ContractService contractService;
  private final LowRemainingDaysAlertService lowRemainingDaysAlertService;

  @GetMapping("/daily-execution")
  public String getDailyExecutionForm(Model model) {
    var sortedMissions = thMissionService.sortedMissionsWithoutMissionExecution();
    model.addAttribute("missions", sortedMissions);
    return "daily-execution";
  }

  public String createDailyExecution(Authentication authentication, ThDailyExecutionForm dmeForm) {
    return createDailyExecution(
        authentication,
        dmeForm,
        RedirectAttributesModelMap());
  }

  @PostMapping("/daily-execution")
  public String createDailyExecution(
      Authentication authentication,
      ThDailyExecutionForm dmeForm,
      RedirectAttributes redirectAttributes) {
    var worker = workerFromAuthentication.apply(authentication).get();
    var remainingDays = contractService.getRemainingDaysByWorker(worker);
    if (remainingDays <= 0) {
      throw new IllegalStateException(
          "Vous n'avez plus de jours disponibles sur votre contrat. Veuillez contacter votre"
              + " administrateur.");
    }

    var dailyExecution = thDailyExecutionFormMapper.toDomain(dmeForm, worker);
    dailyExecutionRepository.save(dailyExecution);

    var remainingDaysAfter = contractService.getRemainingDaysByWorker(worker);
    var contracts = contractService.getAllContractsByWorker(worker);
    var activeContractOpt = contracts.stream().filter(c -> c.duration() != null).findFirst();

    if (activeContractOpt.isPresent()
        && remainingDaysAfter < lowRemainingDaysAlertService.getLowRemainingDaysThreshold()) {
      lowRemainingDaysAlertService.checkAndAlert(
          worker, activeContractOpt.get(), (long) remainingDaysAfter);
      redirectAttributes.addFlashAttribute(
          "toastMessage",
          "Attention : Il vous reste "
              + (long) remainingDaysAfter
              + " jour(s) sur votre contrat !");
      redirectAttributes.addFlashAttribute("toastType", "warning");
    }

    return "redirect:/work-and-care-calendar";
  }
}
