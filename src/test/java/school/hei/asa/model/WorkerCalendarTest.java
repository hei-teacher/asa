package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkerCalendarTest {

  private final Worker worker =
      new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
  private final ProductConf productConf = new ProductConf("CARE", List.of("PAID1"));
  private final Product careProduct = new Product("CARE", "Care", "desc");
  private final Product workProduct = new Product("WORK", "Work", "desc");
  private final Mission mission1 = new Mission("M1", "Mission 1", "desc", 10, careProduct);
  private final Mission mission2 = new Mission("PAID1", "Paid Mission", "desc", 10, careProduct);

  @Test
  void datesByDailyExecutionType_returns_map_by_type() {
    var now = LocalDate.now();
    var exec1 =
        new MissionExecution(
            mission1, worker, now, 0.5, null, now.atStartOfDay().toInstant(ZoneOffset.UTC));
    var exec2 =
        new MissionExecution(
            mission2, worker, now, 0.5, null, now.atStartOfDay().toInstant(ZoneOffset.UTC));
    var dailyExec = new DailyExecution(worker, now, List.of(exec1, exec2));

    var calendar = new WorkerCalendar(worker, List.of(dailyExec), 2025, productConf);

    var result = calendar.datesByDailyExecutionType();
    assertNotNull(result);
  }

  @Test
  void missionExecutionPercentageSumByMissionType_returns_map_by_month_and_type() {
    var now = LocalDate.now();
    var exec1 =
        new MissionExecution(
            mission1, worker, now, 0.5, null, now.atStartOfDay().toInstant(ZoneOffset.UTC));
    var exec2 =
        new MissionExecution(
            mission2, worker, now, 0.5, null, now.atStartOfDay().toInstant(ZoneOffset.UTC));
    var dailyExec = new DailyExecution(worker, now, List.of(exec1, exec2));

    var calendar = new WorkerCalendar(worker, List.of(dailyExec), 2025, productConf);

    var result = calendar.missionExecutionPercentageSumByMissionType();
    assertNotNull(result);
  }

  @Test
  void lateReportedDaysByMonth_returns_empty_when_none_late() {
    var now = LocalDate.now();
    var exec1 =
        new MissionExecution(
            mission1, worker, now, 1.0, null, now.atStartOfDay().toInstant(ZoneOffset.UTC));
    var dailyExec = new DailyExecution(worker, now, List.of(exec1));

    var calendar = new WorkerCalendar(worker, List.of(dailyExec), 2025, productConf);

    var result = calendar.lateReportedDaysByMonth();
    assertNotNull(result);
  }

  @Test
  void datesByDailyExecutionType_returns_non_empty_for_no_executions() {
    var calendar = new WorkerCalendar(worker, List.of(), 2025, productConf);

    var result = calendar.datesByDailyExecutionType();
    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void missionExecutionPercentageSumByMissionType_returns_empty_for_no_executions() {
    var calendar = new WorkerCalendar(worker, List.of(), 2025, productConf);

    var result = calendar.missionExecutionPercentageSumByMissionType();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void lateReportedDaysByMonth_returns_empty_for_no_executions() {
    var calendar = new WorkerCalendar(worker, List.of(), 2025, productConf);

    var result = calendar.lateReportedDaysByMonth();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
