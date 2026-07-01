package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.InternetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.service.mapper.InternetAddressMapper;

class LowRemainingDaysAlertServiceTest {

  private Mailer mailer;
  private InternetAddressMapper internetAddressMapper;
  private LowRemainingDaysAlertService service;

  @BeforeEach
  void setUp() throws Exception {
    mailer = mock(Mailer.class);
    internetAddressMapper = mock(InternetAddressMapper.class);
    when(internetAddressMapper.toInternetAddresses(any()))
        .thenReturn(List.of(new InternetAddress("acc@test.com")));

    service = new LowRemainingDaysAlertService(mailer, "acc@test.com", 10, internetAddressMapper);
  }

  @Test
  void threshold_is_correct() {
    assertEquals(10, service.getLowRemainingDaysThreshold());
  }

  @Test
  void check_and_alert_does_not_send_mail_if_above_threshold() {
    Worker worker =
        new Worker("W-1", "Name", "email@test.com", "Full Name", "Addr", "City", "NIF", "STAT");
    ContractLevel level = mock(ContractLevel.class);
    Contract contract =
        new Contract(
            worker, "Job", level, Instant.now(), null, Duration.ofDays(30), "Company", "key");

    service.checkAndAlert(worker, contract, 15);

    verify(mailer, never()).accept(any());
  }

  @Test
  void check_and_alert_sends_mail_if_below_threshold() throws Exception {
    Worker worker =
        new Worker("W-1", "Name", "email@test.com", "Full Name", "Addr", "City", "NIF", "STAT");
    ContractLevel level = mock(ContractLevel.class);
    Contract contract =
        new Contract(
            worker, "Job", level, Instant.now(), null, Duration.ofDays(30), "Company", "key");

    service.checkAndAlert(worker, contract, 5);

    verify(mailer).accept(any());
  }

  @Test
  void check_and_alert_handles_null_worker_email() throws Exception {
    Worker worker = new Worker("W-1", "Name", null, "Full Name", "Addr", "City", "NIF", "STAT");
    ContractLevel level = mock(ContractLevel.class);
    Contract contract =
        new Contract(
            worker, "Job", level, Instant.now(), null, Duration.ofDays(30), "Company", "key");

    service.checkAndAlert(worker, contract, 5);

    verify(mailer).accept(any());
  }
}
