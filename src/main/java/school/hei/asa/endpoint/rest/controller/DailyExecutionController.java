package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.ThMissionService;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.service.ContractService;

@Controller
@AllArgsConstructor
public class DailyExecutionController {
  private final ThDailyExecutionFormMapper thDailyExecutionFormMapper;
  private final DailyExecutionRepository dailyExecutionRepository;
  private final WorkerFromAuthentication workerFromAuthentication;
  private final ThMissionService thMissionService;
  private final ContractService contractService;

  @GetMapping("/daily-execution")
  public String getDailyExecutionForm(Model model) {
    var sortedMissions = thMissionService.sortedMissionsWithoutMissionExecution();
    model.addAttribute("missions", sortedMissions);
    return "daily-execution";
  }

  public String createDailyExecution(Authentication authentication, ThDailyExecutionForm dmeForm) {
    return createDailyExecution(authentication, dmeForm, new ExtendedModelMap());
  }

  @PostMapping("/daily-execution")
  public String createDailyExecution(
      Authentication authentication, ThDailyExecutionForm dmeForm, Model model) {
    var worker = workerFromAuthentication.apply(authentication).get();
    var remainingDays = contractService.getRemainingDaysByWorker(worker);
    if (remainingDays <= 0) {
      throw new IllegalStateException(
          "Vous n'avez plus de jours disponibles sur votre contrat. Veuillez contacter votre administrateur.");
    }

    var dailyExecution = thDailyExecutionFormMapper.toDomain(dmeForm, worker);
    dailyExecutionRepository.save(dailyExecution);
    return "redirect:/work-and-care-calendar";
  }
}
