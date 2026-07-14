package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.LowRemainingDaysAlertRequested;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;

class LowRemainingDaysAlertServiceTest {

  private EventProducer<LowRemainingDaysAlertRequested> eventProducer;
  private ContractService contractService;
  private LowRemainingDaysAlertService service;

  @BeforeEach
  void setUp() {
    eventProducer = mock(EventProducer.class);
    contractService = mock(ContractService.class);

    service = new LowRemainingDaysAlertService(eventProducer, 10, contractService);
  }

  @Test
  void check_and_alert_does_not_send_event_if_above_threshold() {
    service.checkAndAlert(worker(), 15);

    verify(eventProducer, never()).accept(any());
  }

  @Test
  void check_and_alert_sends_event_if_below_threshold() {
    service.checkAndAlert(worker(), 5);

    ArgumentCaptor<Collection<LowRemainingDaysAlertRequested>> captor =
        ArgumentCaptor.forClass(Collection.class);
    verify(eventProducer).accept(captor.capture());
    var event = List.copyOf(captor.getValue()).getFirst();
    assertEquals("W-1", event.getWorkerCode());
    assertEquals(5, event.getRemainingDays());
  }

  @Test
  void check_and_alert_handles_null_worker_email() {
    var worker = new Worker("W-1", "Name", null, "Full Name", "Addr", "City", "NIF", "STAT");

    service.checkAndAlert(worker, 5);

    verify(eventProducer).accept(any());
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
    verify(eventProducer, never()).accept(any());
  }

  @Test
  void check_remaining_days_returns_alert_message_when_below_threshold() {
    var worker = worker();
    var contract = contract(worker);
    when(contractService.getActiveContractOrThrow(worker)).thenReturn(contract);
    when(contractService.getRemainingDaysForContract(worker, contract)).thenReturn(5d);

    Optional<String> message = service.checkRemainingDaysAndBuildAlertMessage(worker);

    assertEquals(Optional.of("Please note : You have 5 day(s) left on your contract !"), message);
    verify(eventProducer).accept(any());
  }

  @Test
  void check_remaining_days_returns_empty_when_above_threshold() {
    var worker = worker();
    var contract = contract(worker);
    when(contractService.getActiveContractOrThrow(worker)).thenReturn(contract);
    when(contractService.getRemainingDaysForContract(worker, contract)).thenReturn(15d);

    Optional<String> message = service.checkRemainingDaysAndBuildAlertMessage(worker);

    assertEquals(Optional.empty(), message);
    verify(eventProducer, never()).accept(any());
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
