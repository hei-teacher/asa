package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.DailyExecutionRepository;

class CalendarServiceTest {

  private final DailyExecutionRepository dailyExecutionRepository =
      mock(DailyExecutionRepository.class);
  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier =
      mock(PaidCareMissionCodesSupplier.class);
  private final Mailer mailer = mock(Mailer.class);
  private final CalendarService calendarService =
      new CalendarService(
          dailyExecutionRepository, careProductCodeSupplier, paidCareMissionCodesSupplier, mailer);

  @Test
  void datesByDailyExecutionType() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of());

    var result = calendarService.datesByDailyExecutionType(worker, 2025);

    assertNotNull(result);
  }

  @Test
  void missionExecutionPercentageSumByMissionType() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of());

    var result = calendarService.missionExecutionPercentageSumByMissionType(worker, 2025);

    assertNotNull(result);
  }

  @Test
  void lateReportedDaysByMonth() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of());

    var result = calendarService.lateReportedDaysByMonth(worker, 2025);

    assertNotNull(result);
  }
}
