package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;

@Slf4j
class SensitiveWorkerFilterTest extends FacadeIT {
  @Autowired SensitiveWorkerFilter sensitiveWorkerFilter;

  @Test
  void sensitive_worker_sees_own_executions_but_not_other_sensitive_workers() {
    var worker1 = new Worker("W-059", null, null, null, null, null, null, null);
    var worker2 = new Worker("W-038", null, null, null, null, null, null, null);
    var worker3 = new Worker("W-037", null, null, null, null, null, null, null);

    var de1 =
        new DailyExecution(
            worker1,
            LocalDate.now(),
            List.of(new MissionExecution(null, null, null, 1, null, null)));
    var de2 =
        new DailyExecution(
            worker2,
            LocalDate.now(),
            List.of(new MissionExecution(null, null, null, 1, null, null)));
    var de3 =
        new DailyExecution(
            worker3,
            LocalDate.now(),
            List.of(new MissionExecution(null, null, null, 1, null, null)));

    var result =
        sensitiveWorkerFilter.filterMissionExecutionsWithoutSensitiveWorkers(
            List.of(de1, de2, de3), worker1.code());

    log.info("result : {}", result);

    assertEquals(3, result.size());
    assertTrue(result.stream().anyMatch(de -> de.worker().code().equals("W-037")));
    assertTrue(result.stream().anyMatch(de -> de.worker().code().equals("W-038")));
    assertTrue(result.stream().anyMatch(de -> de1.worker().code().equals("W-059")));
  }

  @Test
  void normal_worker_can_not_see_sensitive_workers_daily_executions() {
    var worker1 = new Worker("W-059", null, null, null, null, null, null, null);
    var worker2 = new Worker("W-038", null, null, null, null, null, null, null);
    var worker3 = new Worker("W-037", null, null, null, null, null, null, null);

    var de1 =
        new DailyExecution(
            worker1,
            LocalDate.now(),
            List.of(new MissionExecution(null, null, null, 1, null, null)));
    var de2 =
        new DailyExecution(
            worker2,
            LocalDate.now(),
            List.of(new MissionExecution(null, null, null, 1, null, null)));
    var de3 =
        new DailyExecution(
            worker3,
            LocalDate.now(),
            List.of(new MissionExecution(null, null, null, 1, null, null)));

    var result =
        sensitiveWorkerFilter.filterMissionExecutionsWithoutSensitiveWorkers(
            List.of(de1, de2, de3), worker2.code());

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(de -> de.worker().code().equals("W-037")));
    assertTrue(result.stream().anyMatch(de -> de.worker().code().equals("W-038")));
    assertFalse(result.stream().anyMatch(de -> de.worker().code().equals("W-059")));
  }

  @Test
  void normal_worker_can_not_select_sensitives_workers() {
    var worker1 = new Worker("W-059", null, null, null, null, null, null, null);
    var worker2 = new Worker("W-038", null, null, null, null, null, null, null);
    var worker3 = new Worker("W-037", null, null, null, null, null, null, null);

    var actual =
        sensitiveWorkerFilter.filterSensitiveWorkers(
            List.of(worker1, worker2, worker3), worker2.code());

    assertFalse(actual.stream().anyMatch(worker -> worker.code().equals("W-059")));
    assertTrue(actual.stream().anyMatch(worker -> worker.code().equals("W-037")));
    assertTrue(actual.stream().anyMatch(worker -> worker.code().equals("W-038")));
  }

  @Test
  void sensitive_worker_can_self_select() {
    var worker1 = new Worker("W-059", null, null, null, null, null, null, null);
    var worker2 = new Worker("W-038", null, null, null, null, null, null, null);
    var worker3 = new Worker("W-037", null, null, null, null, null, null, null);

    var actual =
        sensitiveWorkerFilter.filterSensitiveWorkers(
            List.of(worker1, worker2, worker3), worker1.code());
    assertTrue(actual.stream().anyMatch(worker -> worker.code().equals("W-059")));
    assertTrue(actual.stream().anyMatch(worker -> worker.code().equals("W-037")));
    assertTrue(actual.stream().anyMatch(worker -> worker.code().equals("W-038")));
  }
}
