package school.hei.asa.model;

import static java.lang.Double.parseDouble;
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
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
                List.of(
                    new MissionExecution(mission, worker, now(), 0.2, "comment", Instant.now()))));
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
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.1"), "comment1", Instant.now()),
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.1"), "comment2", Instant.now()),
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.7"), "comment3", Instant.now()),
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.1"), "comment4", Instant.now()))));
  }

  @Test
  void daily_execution_removes_duplicates_and_validates_percentage_sum() {
    var product = new Product("pcode", "pname", "pdescription");
    var worker = new PartnerContractor("worker-code", "name", "email");
    var mission = new Mission("mission-code", "title", "description", 10, product);
    var now = Instant.now();

    assertDoesNotThrow(
        () ->
            new DailyExecution(
                worker,
                now(),
                List.of(
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.1"), "comment1", now),
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.1"), "comment2", now),
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.7"), "comment3", now),
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.1"), "comment4", now),
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.1"), "comment1", now),
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.1"), "comment2", now),
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.7"), "comment3", now),
                    new MissionExecution(
                        mission, worker, now(), parseDouble("0.1"), "comment4", now))));
  }
}
