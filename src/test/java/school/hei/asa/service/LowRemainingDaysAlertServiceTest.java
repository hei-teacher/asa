package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.InternetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
  private ContractService contractService;
  private LowRemainingDaysAlertService service;

  @BeforeEach
  void setUp() throws Exception {
    mailer = mock(Mailer.class);
    internetAddressMapper = mock(InternetAddressMapper.class);
    contractService = mock(ContractService.class);
    when(internetAddressMapper.toInternetAddresses(any()))
        .thenReturn(List.of(new InternetAddress("acc@test.com")));

    service =
        new LowRemainingDaysAlertService(
            mailer, "acc@test.com", 10, internetAddressMapper, contractService);
  }

  @Test
  void check_and_alert_does_not_send_mail_if_above_threshold() {
    var worker = worker();
    var contract = contract(worker);

    service.checkAndAlert(worker, contract, 15);

    verify(mailer, never()).accept(any());
  }

  @Test
  void check_and_alert_sends_mail_if_below_threshold() {
    var worker = worker();
    var contract = contract(worker);

    service.checkAndAlert(worker, contract, 5);

    verify(mailer).accept(any());
  }

  @Test
  void check_and_alert_handles_null_worker_email() {
    var worker = new Worker("W-1", "Name", null, "Full Name", "Addr", "City", "NIF", "STAT");
    var contract = contract(worker);

    service.checkAndAlert(worker, contract, 5);

    verify(mailer).accept(any());
  }

  @Test
  void check_remaining_days_throws_when_no_active_contract() {
    var worker = worker();
    when(contractService.getActiveContractOrThrow(worker))
        .thenThrow(
            new IllegalStateException(
                "You do not have an active contract. Please contact your administrator."));

    var exception =
        assertThrows(
            IllegalStateException.class,
            () -> service.checkRemainingDaysAndBuildAlertMessage(worker));

    assertEquals(
        "You do not have an active contract. Please contact your administrator.",
        exception.getMessage());
    verify(mailer, never()).accept(any());
  }

  @Test
  void check_remaining_days_throws_when_no_days_left() {
    var worker = worker();
    var contract = contract(worker);
    when(contractService.getActiveContractOrThrow(worker)).thenReturn(contract);
    when(contractService.getRemainingDaysByWorker(worker, contract)).thenReturn(0d);

    var exception =
        assertThrows(
            IllegalStateException.class,
            () -> service.checkRemainingDaysAndBuildAlertMessage(worker));

    assertTrue(exception.getMessage().contains("no more days available"));
    verify(mailer, never()).accept(any());
  }

  @Test
  void check_remaining_days_returns_alert_message_when_below_threshold() {
    var worker = worker();
    var contract = contract(worker);
    when(contractService.getActiveContractOrThrow(worker)).thenReturn(contract);
    when(contractService.getRemainingDaysByWorker(worker, contract)).thenReturn(5d);

    Optional<String> message = service.checkRemainingDaysAndBuildAlertMessage(worker);

    assertEquals(Optional.of("Please note : You have 5 day(s) left on your contract !"), message);
    verify(mailer).accept(any());
  }

  @Test
  void check_remaining_days_returns_empty_when_above_threshold() {
    var worker = worker();
    var contract = contract(worker);
    when(contractService.getActiveContractOrThrow(worker)).thenReturn(contract);
    when(contractService.getRemainingDaysByWorker(worker, contract)).thenReturn(15d);

    Optional<String> message = service.checkRemainingDaysAndBuildAlertMessage(worker);

    assertEquals(Optional.empty(), message);
    verify(mailer, never()).accept(any());
  }

  private static Worker worker() {
    return new Worker("W-1", "Name", "email@test.com", "Full Name", "Addr", "City", "NIF", "STAT");
  }

  private static Contract contract(Worker worker) {
    ContractLevel level = mock(ContractLevel.class);
    return new Contract(
        worker, "Job", level, Instant.now(), null, Duration.ofDays(30), "Company", "key");
  }
}
