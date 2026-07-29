package school.hei.asa.endpoint.rest.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.DailyExecutionService;
import school.hei.asa.endpoint.rest.service.ThMissionService;
import school.hei.asa.service.ContractAlertService;
import school.hei.asa.service.ContractExhaustedException;
import school.hei.asa.service.ContractService;

@Controller
public class DailyExecutionController {
  private final WorkerFromAuthentication workerFromAuthentication;
  private final ThMissionService thMissionService;
  private final ContractService contractService;
  private final DailyExecutionService dailyExecutionService;
  private final ContractAlertService contractAlertService;
  private final int alertThreshold;

  public DailyExecutionController(
      WorkerFromAuthentication workerFromAuthentication,
      ThMissionService thMissionService,
      ContractService contractService,
      DailyExecutionService dailyExecutionService,
      ContractAlertService contractAlertService,
      @Value("${ASA_CONTRACT_ALERT_THRESHOLD}") int alertThreshold) {
    this.workerFromAuthentication = workerFromAuthentication;
    this.thMissionService = thMissionService;
    this.contractService = contractService;
    this.dailyExecutionService = dailyExecutionService;
    this.contractAlertService = contractAlertService;
    this.alertThreshold = alertThreshold;
  }

  @GetMapping("/daily-execution")
  public String getDailyExecutionForm(Authentication authentication, Model model) {
    var sortedMissions = thMissionService.sortedMissionsWithoutMissionExecution();
    model.addAttribute("missions", sortedMissions);

    var worker = workerFromAuthentication.apply(authentication).get();
    contractAlertService
        .contractAlertMessage(worker, alertThreshold)
        .ifPresent(msg -> model.addAttribute("contractAlert", msg));

    return "daily-execution";
  }

  @PostMapping("/daily-execution")
  public String createDailyExecution(
      Authentication authentication,
      ThDailyExecutionForm dmeForm,
      RedirectAttributes redirectAttributes) {
    var worker = workerFromAuthentication.apply(authentication).get();
    dailyExecutionService.saveAndAlert(dmeForm, worker);
    return "redirect:/work-and-care-calendar";
  }

  @ExceptionHandler(ContractExhaustedException.class)
  public String handleContractExhausted(ContractExhaustedException e, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("error", e.getMessage());
    return "redirect:/daily-execution";
  }
}
