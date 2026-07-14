package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.*;
import school.hei.asa.repository.DailyExecutionRepository;

class CalendarServiceConcreteTest {

  private final DailyExecutionRepository dailyExecutionRepository =
      mock(DailyExecutionRepository.class);
  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier =
      mock(PaidCareMissionCodesSupplier.class);
  private final Mailer mailer = mock(Mailer.class);
  private final CalendarService service =
      new CalendarService(
          dailyExecutionRepository, careProductCodeSupplier, paidCareMissionCodesSupplier, mailer);

  private final Worker worker =
      new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

  @Test
  void datesByDailyExecutionType_empty() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of());

    var result = service.datesByDailyExecutionType(worker, 2025);

    assertEquals(3, result.size());
    assertTrue(result.values().stream().allMatch(List::isEmpty));
  }

  @Test
  void datesByDailyExecutionType_with_executions() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    var product = new Product("WORK", "Work", "D");
    var mission = new Mission("M01", "M", "D", 10, product);
    var date = LocalDate.of(2025, 6, 1);
    var me = new MissionExecution(mission, worker, date, 1.0, "c", Instant.now());
    var de = new DailyExecution(worker, date, List.of(me));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));

    var result = service.datesByDailyExecutionType(worker, 2025);

    assertEquals(1, result.get(DailyExecution.Type.fullWork).size());
  }

  @Test
  void lateReportedDaysByMonth_empty() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of());

    var result = service.lateReportedDaysByMonth(worker, 2025);

    assertTrue(result.isEmpty());
  }
}
