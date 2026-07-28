package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import school.hei.asa.service.ContractAlertService;

class WorkerCalendarTest {

  private static final int THRESHOLD = 10;
  private static final Worker WORKER =
      new Worker("test-code", null, null, null, null, null, null, null);
  private static final WorkerCalendar CALENDAR = new WorkerCalendar(WORKER, List.of(), 2026, null);

  private final ContractAlertService contractAlertService = mock(ContractAlertService.class);

  @Test
  void contract_exhausted() {
    when(contractAlertService.contractAlertMessage(eq(WORKER), eq(THRESHOLD)))
        .thenReturn(Optional.of("Your contract has no remaining days left."));

    var result = CALENDAR.contractAlertMessage(contractAlertService, THRESHOLD);

    assertEquals("Your contract has no remaining days left.", result.get());
  }

  @Test
  void contract_overdue() {
    when(contractAlertService.contractAlertMessage(eq(WORKER), eq(THRESHOLD)))
        .thenReturn(Optional.of("Your contract is overdue."));

    var result = CALENDAR.contractAlertMessage(contractAlertService, THRESHOLD);

    assertEquals("Your contract is overdue.", result.get());
  }

  @Test
  void contract_near_expiry() {
    when(contractAlertService.contractAlertMessage(eq(WORKER), eq(THRESHOLD)))
        .thenReturn(Optional.of("Warning: only 3 days left on your contract."));

    var result = CALENDAR.contractAlertMessage(contractAlertService, THRESHOLD);

    assertEquals("Warning: only 3 days left on your contract.", result.get());
  }

  @Test
  void contract_near_expiry_singular() {
    when(contractAlertService.contractAlertMessage(eq(WORKER), eq(THRESHOLD)))
        .thenReturn(Optional.of("Warning: only 1 day left on your contract."));

    var result = CALENDAR.contractAlertMessage(contractAlertService, THRESHOLD);

    assertEquals("Warning: only 1 day left on your contract.", result.get());
  }

  @Test
  void contract_ok() {
    when(contractAlertService.contractAlertMessage(eq(WORKER), eq(THRESHOLD)))
        .thenReturn(Optional.empty());

    var result = CALENDAR.contractAlertMessage(contractAlertService, THRESHOLD);

    assertTrue(result.isEmpty());
  }

  @Test
  void contract_ok_at_threshold() {
    when(contractAlertService.contractAlertMessage(eq(WORKER), eq(THRESHOLD)))
        .thenReturn(Optional.empty());

    var result = CALENDAR.contractAlertMessage(contractAlertService, THRESHOLD);

    assertTrue(result.isEmpty());
  }
}
