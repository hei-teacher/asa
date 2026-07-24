package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

class LowRemainingDaysAlertServiceTest {

  private EventProducer<LowRemainingDaysAlertRequested> eventProducer;
  private ContractService contractService;
  private LowRemainingDaysAlertService service;

  @BeforeEach
  void setUp() {
    eventProducer = mock(EventProducer.class);
    contractService = mock(ContractService.class);
    service = new LowRemainingDaysAlertService(eventProducer, contractService, 10);
  }

  @Test
  void check_remaining_days_returns_empty_when_no_active_contract() {
    var worker = worker();
    when(contractService.findActiveContract(worker)).thenReturn(Optional.empty());

    Optional<String> message = service.checkRemainingDaysAndBuildAlertMessage(worker);

    assertEquals(Optional.empty(), message);
    verify(eventProducer, never()).accept(any());
  }

  @Test
  void check_remaining_days_returns_alert_message_when_below_threshold() {
    var worker = worker();
    when(contractService.findActiveContract(worker)).thenReturn(Optional.of(mock(Contract.class)));
    when(contractService.getRemainingDaysOnActiveContractOrZero(worker)).thenReturn(5d);

    Optional<String> message = service.checkRemainingDaysAndBuildAlertMessage(worker);

    assertEquals(Optional.of("Please note : You have 5 day(s) left on your contract !"), message);
    ArgumentCaptor<Collection<LowRemainingDaysAlertRequested>> captor =
        ArgumentCaptor.forClass(Collection.class);
    verify(eventProducer).accept(captor.capture());
    var event = List.copyOf(captor.getValue()).getFirst();
    assertEquals("W-1", event.getWorkerCode());
    assertEquals(5, event.getRemainingDays());
  }

  @Test
  void check_remaining_days_returns_empty_when_above_threshold() {
    var worker = worker();
    when(contractService.findActiveContract(worker)).thenReturn(Optional.of(mock(Contract.class)));
    when(contractService.getRemainingDaysOnActiveContractOrZero(worker)).thenReturn(15d);

    Optional<String> message = service.checkRemainingDaysAndBuildAlertMessage(worker);

    assertEquals(Optional.empty(), message);
    verify(eventProducer, never()).accept(any());
  }

  @Test
  void check_remaining_days_returns_empty_when_remaining_days_are_zero() {
    var worker = worker();
    when(contractService.findActiveContract(worker)).thenReturn(Optional.of(mock(Contract.class)));
    when(contractService.getRemainingDaysOnActiveContractOrZero(worker)).thenReturn(0d);

    Optional<String> message = service.checkRemainingDaysAndBuildAlertMessage(worker);

    assertEquals(Optional.empty(), message);
    verify(eventProducer, never()).accept(any());
  }

  @Test
  void is_below_threshold_returns_true_when_remaining_days_under_threshold() {
    assertTrue(service.isBelowThreshold(5));
  }

  @Test
  void is_below_threshold_returns_false_when_remaining_days_are_zero_or_negative() {
    assertFalse(service.isBelowThreshold(0));
    assertFalse(service.isBelowThreshold(-1));
  }

  @Test
  void is_below_threshold_returns_false_when_remaining_days_at_or_above_threshold() {
    assertFalse(service.isBelowThreshold(10));
    assertFalse(service.isBelowThreshold(15));
  }

  private static Worker worker() {
    return new Worker("W-1", "Name", "email@test.com", "Full Name", "Addr", "City", "NIF", "STAT");
  }
}
