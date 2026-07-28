package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.service.ContractService;

class WorkerCalendarTest {

  private static final int THRESHOLD = 10;
  private static final Worker WORKER =
      new Worker("test-code", null, null, null, null, null, null, null);
  private static final WorkerCalendar CALENDAR =
      new WorkerCalendar(WORKER, List.of(), 2026, null);

  private final ContractService contractService = mock(ContractService.class);

  @Test
  void contract_exhausted() {
    when(contractService.getRemainingDaysByWorker(WORKER)).thenReturn(0L);

    var result = CALENDAR.contractAlertMessage(contractService, THRESHOLD);

    assertEquals("Your contract has no remaining days left.", result.get());
  }

  @Test
  void contract_overdue() {
    when(contractService.getRemainingDaysByWorker(WORKER)).thenReturn(-1L);

    var result = CALENDAR.contractAlertMessage(contractService, THRESHOLD);

    assertEquals("Your contract is overdue.", result.get());
  }

  @Test
  void contract_near_expiry() {
    when(contractService.getRemainingDaysByWorker(WORKER)).thenReturn(3L);

    var result = CALENDAR.contractAlertMessage(contractService, THRESHOLD);

    assertEquals("Warning: only 3 days left on your contract.", result.get());
  }

  @Test
  void contract_near_expiry_singular() {
    when(contractService.getRemainingDaysByWorker(WORKER)).thenReturn(1L);

    var result = CALENDAR.contractAlertMessage(contractService, THRESHOLD);

    assertEquals("Warning: only 1 day left on your contract.", result.get());
  }

  @Test
  void contract_ok() {
    when(contractService.getRemainingDaysByWorker(WORKER)).thenReturn(15L);

    var result = CALENDAR.contractAlertMessage(contractService, THRESHOLD);

    assertTrue(result.isEmpty());
  }

  @Test
  void contract_ok_at_threshold() {
    when(contractService.getRemainingDaysByWorker(WORKER)).thenReturn((long) THRESHOLD);

    var result = CALENDAR.contractAlertMessage(contractService, THRESHOLD);

    assertTrue(result.isEmpty());
  }
}