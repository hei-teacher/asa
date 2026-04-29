package school.hei.asa.endpoint.rest.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.event.model.LateReportedDaysVerificationRequested;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.event.LateReportedDaysVerificationService;

public class LateReportedDayServiceTest extends FacadeIT {

  @Autowired LateReportedDaysVerificationService lateReportedDaysVerificationService;
  @MockBean ContractService contractService;
  @MockBean Mailer mailer;

  @MockBean MissionExecutionRepository missionExecutionRepository;

  @Test
  public void extract_not_reporting_worker_ok() {
    var worker1 = new Worker("W-37", "name", "email", "fullNAme", "address", "city", "nif", "stat");
    var worker2 = new Worker("W-36", "name", "email", "fullNAme", "address", "city", "nif", "stat");
    var workerWhoReported = List.of("W-036"); // those who didn't
    var contractList =
        List.of(
            new Contract(
                worker1, "", null, Instant.now(), Instant.now(), Duration.ofDays(2), "", ""),
            new Contract(worker2, "", null, null, null, null, "", ""));
    when(contractService.findActiveContract()).thenReturn(contractList);

    var subject =
        lateReportedDaysVerificationService.extractWorkersWhoDidNotReport(workerWhoReported);
    assertTrue(subject.contains(worker1));
  }

  @Test
  public void send_email_to_unreported_worker_ok() {
    LocalDate date = LocalDate.of(2024, 6, 15);
    List<String> receivers = List.of("worker1@test.com");

    lateReportedDaysVerificationService.sendEmailToUnReportedWorkers(receivers, date);

    ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
    verify(mailer, times(1)).accept(emailCaptor.capture());

    Email sentEmail = emailCaptor.getValue();
    assertEquals("ASA - LATE REPORTED WORK ON 2024-06-15", sentEmail.subject());
    assertTrue(sentEmail.htmlBody().contains("2024-06-15"));
  }

  @Test
  public void late_reported_days_check_ok() {
    var worker1 = new Worker("W-36", "name", "email", "fullNAme", "address", "city", "nif", "stat");
    var worker2 = new Worker("W-37", "name", "email", "fullNAme", "address", "city", "nif", "stat");

    when(contractService.findActiveContract())
        .thenReturn(
            List.of(
                new Contract(
                    worker1, "", null, Instant.now(), Instant.now(), Duration.ofDays(2), "", ""),
                new Contract(worker2, "", null, null, null, null, "", "")));
    when(missionExecutionRepository.findWorkerCodeByDate(any())).thenReturn(List.of("code1"));

    lateReportedDaysVerificationService.accept(new LateReportedDaysVerificationRequested());

    verify(mailer, times(2)).accept(any(Email.class));
  }
}
