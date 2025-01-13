package school.hei.asa.model;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DailyExecutionTest {

  @Test
  void missionPercentagesSum_lt1_isIllegal() {
    var product = new Product("pcode", "pname", "pdescription");
    var worker = new PartnerContractor("worker-code", "name", "email");
    var mission = new Mission("mission-code", "title", "description", 10, product);
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new DailyExecution(
                worker,
                now(),
                List.of(new MissionExecution(mission, worker, now(), 0.2, "comment"))));
  }

  @Test
  @DisplayName("should not throw when percentages sum to exactly 100")
  void missionPercentageSum_float_ok() {
    var product = new Product("pcode", "pname", "pdescription");
    var worker = new PartnerContractor("worker-code", "name", "email");
    var mission = new Mission("mission-code", "title", "description", 10, product);

    var dailyExecution =
        new DailyExecution(
            worker,
            now(),
            List.of(
                new MissionExecution(mission, worker, now(), 0.1, "comment"),
                new MissionExecution(mission, worker, now(), 0.2, "comment"),
                new MissionExecution(mission, worker, now(), 0.1, "comment"),
                new MissionExecution(mission, worker, now(), 0.2, "comment"),
                new MissionExecution(mission, worker, now(), 0.1, "comment"),
                new MissionExecution(mission, worker, now(), 0.2, "comment"),
                new MissionExecution(mission, worker, now(), 0.1, "comment")));

    assertEquals(100, dailyExecution.sumExecutionPercentage());
  }
}
