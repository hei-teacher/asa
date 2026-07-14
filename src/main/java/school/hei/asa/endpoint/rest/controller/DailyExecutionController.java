package school.hei.asa.endpoint.rest.controller;

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

@Controller
public class DailyExecutionController {
  private final ThDailyExecutionFormMapper thDailyExecutionFormMapper;
  private final DailyExecutionRepository dailyExecutionRepository;
  private final WorkerFromAuthentication workerFromAuthentication;
  private final ThMissionService thMissionService;
  private final ContractService contractService;

  public DailyExecutionController(
          ThDailyExecutionFormMapper thDailyExecutionFormMapper,
          DailyExecutionRepository dailyExecutionRepository,
          WorkerFromAuthentication workerFromAuthentication,
          ThMissionService thMissionService,
          ContractService contractService) {
    this.thDailyExecutionFormMapper = thDailyExecutionFormMapper;
    this.dailyExecutionRepository = dailyExecutionRepository;
    this.workerFromAuthentication = workerFromAuthentication;
    this.thMissionService = thMissionService;
    this.contractService = contractService;
  }

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

    if (!contractService.hasRemainingDays(worker)) {
      throw new IllegalArgumentException(
              "Cannot submit report: " + worker.name() + " has no remaining contract days.");
    }

    dailyExecutionRepository.save(thDailyExecutionFormMapper.toDomain(dmeForm, worker));

    contractService
            .checkAndNotifyContractAlert(worker)
            .ifPresent(msg -> redirectAttributes.addFlashAttribute("contractAlert", msg));

    return "redirect:/work-and-care-calendar";
  }
}