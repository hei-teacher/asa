package school.hei.asa.model;

import static java.lang.Double.parseDouble;
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class DailyExecutionTest {

  @Test
  void missionPercentagesSum_lt100_isIllegal() {
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
  void missionPercentageSum_float_ok() {
    var product = new Product("pcode", "pname", "pdescription");
    var worker = new PartnerContractor("worker-code", "name", "email");
    var mission = new Mission("mission-code", "title", "description", 10, product);

    assertDoesNotThrow(
        () ->
            new DailyExecution(
                worker,
                now(),
                List.of(
                    new MissionExecution(mission, worker, now(), parseDouble("0.1"), "comment1"),
                    new MissionExecution(mission, worker, now(), parseDouble("0.1"), "comment2"),
                    new MissionExecution(mission, worker, now(), parseDouble("0.7"), "comment3"),
                    new MissionExecution(mission, worker, now(), parseDouble("0.1"), "comment4"))));
  }

  @Test
  void daily_execution_removes_duplicates_and_validates_percentage_sum() {
    var product = new Product("pcode", "pname", "pdescription");
    var worker = new PartnerContractor("worker-code", "name", "email");
    var mission = new Mission("mission-code", "title", "description", 10, product);

    assertDoesNotThrow(
        () ->
            new DailyExecution(
                worker,
                now(),
                List.of(
                    new MissionExecution(mission, worker, now(), parseDouble("0.1"), "comment1"),
                    new MissionExecution(mission, worker, now(), parseDouble("0.1"), "comment2"),
                    new MissionExecution(mission, worker, now(), parseDouble("0.7"), "comment3"),
                    new MissionExecution(mission, worker, now(), parseDouble("0.1"), "comment4"),
                    new MissionExecution(mission, worker, now(), parseDouble("0.1"), "comment1"),
                    new MissionExecution(mission, worker, now(), parseDouble("0.1"), "comment2"),
                    new MissionExecution(mission, worker, now(), parseDouble("0.7"), "comment3"),
                    new MissionExecution(mission, worker, now(), parseDouble("0.1"), "comment4"))));
  }
}
