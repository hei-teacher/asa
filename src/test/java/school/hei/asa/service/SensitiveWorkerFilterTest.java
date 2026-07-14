package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;

class SensitiveWorkerFilterTest {

  private final Product careProduct = new Product("CARE", "Care", "desc");
  private final Mission mission = new Mission("M1", "Mission 1", "desc", 10, careProduct);
  private SensitiveWorkerFilter filter;
  private Worker worker1;
  private Worker worker2;
  private Worker worker3;

  @BeforeEach
  void setUp() {
    filter = new SensitiveWorkerFilter("SENSITIVE1,SENSITIVE2");
    worker1 =
        new Worker("SENSITIVE1", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    worker2 =
        new Worker("SENSITIVE2", "Jane", "jane@test.com", "Jane", "Addr", "City", "NIF", "STAT");
    worker3 = new Worker("NORMAL1", "Bob", "bob@test.com", "Bob", "Addr", "City", "NIF", "STAT");
  }

  @Test
  void filterSensitiveWorkers_authenticated_sensitive_sees_only_himself() {
    var workers = List.of(worker1, worker2, worker3);
    var result = filter.filterSensitiveWorkers(workers, "SENSITIVE1");
    assertEquals(List.of(worker1, worker3), result);
  }

  @Test
  void filterSensitiveWorkers_authenticated_non_sensitive_hides_all_sensitive() {
    var workers = List.of(worker1, worker2, worker3);
    var result = filter.filterSensitiveWorkers(workers, "NORMAL1");
    assertEquals(List.of(worker3), result);
  }

  @Test
  void filterSensitiveWorkers_authenticated_non_sensitive_with_only_sensitive_returns_empty() {
    var workers = List.of(worker1, worker2);
    var result = filter.filterSensitiveWorkers(workers, "NORMAL1");
    assertTrue(result.isEmpty());
  }

  @Test
  void filterMissionExecutionsWithoutSensitiveWorkers_sensitive_keeps_own_and_normal() {
    var now = LocalDate.now();
    var exec1 =
        new MissionExecution(
            mission, worker1, now, 1.0, null, now.atStartOfDay().toInstant(ZoneOffset.UTC));
    var exec3 =
        new MissionExecution(
            mission, worker3, now, 1.0, null, now.atStartOfDay().toInstant(ZoneOffset.UTC));
    var de1 = new DailyExecution(worker1, now, List.of(exec1));
    var de3 = new DailyExecution(worker3, now, List.of(exec3));
    var executions = List.of(de1, de3);

    var result = filter.filterMissionExecutionsWithoutSensitiveWorkers(executions, "SENSITIVE1");

    assertEquals(2, result.size());
  }

  @Test
  void filterMissionExecutionsWithoutSensitiveWorkers_non_sensitive_hides_all_sensitive() {
    var now = LocalDate.now();
    var exec1 =
        new MissionExecution(
            mission, worker1, now, 1.0, null, now.atStartOfDay().toInstant(ZoneOffset.UTC));
    var exec3 =
        new MissionExecution(
            mission, worker3, now, 1.0, null, now.atStartOfDay().toInstant(ZoneOffset.UTC));
    var de1 = new DailyExecution(worker1, now, List.of(exec1));
    var de3 = new DailyExecution(worker3, now, List.of(exec3));
    var executions = List.of(de1, de3);

    var result = filter.filterMissionExecutionsWithoutSensitiveWorkers(executions, "NORMAL1");

    assertEquals(1, result.size());
  }
}
