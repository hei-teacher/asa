package school.hei.asa.repository.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class WorkerDayPercentageSummaryTest {

  @Test
  void constructor_and_accessors_work() {
    var now = Instant.now();
    var summary = new WorkerDayPercentageSummary("W-001", 0.75, now, "M-001");

    assertEquals("W-001", summary.workerCode());
    assertEquals(0.75, summary.totalDayPercentage());
    assertEquals(now, summary.creationInstant());
    assertEquals("M-001", summary.missionCode());
  }
}
