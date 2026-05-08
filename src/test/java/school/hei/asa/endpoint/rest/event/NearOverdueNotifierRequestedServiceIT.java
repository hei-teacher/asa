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
import school.hei.asa.endpoint.event.model.NearOverdueNotifierRequested;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.event.NearOverdueNotifierRequestedService;

public class NearOverdueNotifierRequestedServiceIT extends FacadeIT {

  @Autowired NearOverdueNotifierRequestedService nearOverdueNotifierRequestedService;
  @MockBean ContractService contractService;
  @MockBean Mailer mailer;

  @MockBean MissionExecutionRepository missionExecutionRepository;

  @Test
  public void send_email_to_unreported_worker_ok() {
    LocalDate date = LocalDate.of(2024, 6, 15);
    var worker1 = new Worker("W-36", "name", "email", "fullNAme", "address", "city", "nif", "stat");

    nearOverdueNotifierRequestedService.sendEmailToWorkersWhoDidNotReportYet(
        List.of(worker1), date);

    ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
    verify(mailer, times(1)).accept(emailCaptor.capture());

    Email sentEmail = emailCaptor.getValue();
    assertEquals("ASA - REMINDER TO REPORT THE DATE 2024-06-15", sentEmail.subject());
    assertTrue(sentEmail.htmlBody().contains("2024-06-15"));
  }

  @Test
  public void late_reported_days_check_ok() {
    var worker1 = new Worker("W-36", "name", "email", "fullNAme", "address", "city", "nif", "stat");
    var worker2 = new Worker("W-37", "name", "email", "fullNAme", "address", "city", "nif", "stat");
    var date = LocalDate.of(2026, 4, 18);
    when(contractService.findActiveContracts())
        .thenReturn(
            List.of(
                new Contract(
                    worker1, "", null, Instant.now(), Instant.now(), Duration.ofDays(2), "", ""),
                new Contract(worker2, "", null, null, null, null, "", "")));
    when(missionExecutionRepository.findWorkerCodesByDate(any(LocalDate.class)))
        .thenReturn(List.of("W-36"));

    nearOverdueNotifierRequestedService.accept(new NearOverdueNotifierRequested(date));
    verify(mailer, times(1)).accept(any(Email.class));
  }
}
