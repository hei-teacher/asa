package school.hei.asa.endpoint.rest.controller;


import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Slf4j
@Controller
public class DailyExecutionController {
  private final ThDailyExecutionFormMapper thDailyExecutionFormMapper;
  private final DailyExecutionRepository dailyExecutionRepository;
  private final WorkerFromAuthentication workerFromAuthentication;
  private final ThMissionService thMissionService;
  private final ContractService contractService;
  private final Mailer mailer;
  private final InternetAddressMapper internetAddressMapper;
  private final String accountants;
  private final int alertThreshold;

  public DailyExecutionController(
      ThDailyExecutionFormMapper thDailyExecutionFormMapper,
      DailyExecutionRepository dailyExecutionRepository,
      WorkerFromAuthentication workerFromAuthentication,
      ThMissionService thMissionService,
      ContractService contractService,
      Mailer mailer,
      InternetAddressMapper internetAddressMapper,
      @Value("${ACCOUNTANTS}") String accountants,
      @Value("${asa.contract.alert.threshold}") int alertThreshold) {
    this.thDailyExecutionFormMapper = thDailyExecutionFormMapper;
    this.dailyExecutionRepository = dailyExecutionRepository;
    this.workerFromAuthentication = workerFromAuthentication;
    this.thMissionService = thMissionService;
    this.contractService = contractService;
    this.mailer = mailer;
    this.internetAddressMapper = internetAddressMapper;
    this.accountants = accountants;
    this.alertThreshold = alertThreshold;
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
          "Cannot submit pointage: " + worker.name() + " has no remaining contract days.");
    }

    var dailyExecution = thDailyExecutionFormMapper.toDomain(dmeForm, worker);
    dailyExecutionRepository.save(dailyExecution);

    var remaining = contractService.remainingDays(worker);
    if (remaining >= 0 && remaining < alertThreshold) {
      var msg =
          "Warning: only "
              + remaining
              + " day"
              + (remaining > 1 ? "s" : "")
              + " left on your contract.";
      redirectAttributes.addFlashAttribute("contractAlert", msg);
      try {
        sendAlertEmail(worker, remaining);
      } catch (Exception e) {
        log.error("Failed to send contract alert email", e);
      }
    }

    return "redirect:/work-and-care-calendar";
  }

  private void sendAlertEmail(school.hei.asa.model.Worker worker, long remaining) {
    var accountantEmails =
        internetAddressMapper.toInternetAddresses(Arrays.asList(accountants.split(",")));
    if (accountantEmails.isEmpty()) {
      return;
    }
    var subject = "ASA - CONTRACT ALERT - " + worker.name() + " - Only " + remaining + " days left";
    var body =
        "Hello,<br><br>"
            + "The contract of <b>"
            + worker.name()
            + "</b> ("
            + worker.email()
            + ") is about to expire.<br>"
            + "Only <b>"
            + remaining
            + "</b> day"
            + (remaining > 1 ? "s" : "")
            + " left on the contract.<br><br>"
            + "Best regards,<br>ASA";
    mailer.accept(
        new Email(
            accountantEmails.getFirst(),
            accountantEmails.size() > 1
                ? accountantEmails.subList(1, accountantEmails.size())
                : List.of(),
            List.of(),
            subject,
            body,
            List.of()));
  }
}
