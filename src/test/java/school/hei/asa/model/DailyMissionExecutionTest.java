package school.hei.asa.model;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;

class DailyMissionExecutionTest {

  @Test
  void missionPercentagesSum_lt1_isIllegal() {
    var worker = new PartnerContractor("worker-code", "name");
    var mission = new Mission("mission-code", "title", "description", 10);
    assertThrows(
        IllegalArgumentException.class,
        () -> new DailyMissionExecution(worker, now(), Map.of(mission, 0.2)));
  }
}
