package school.hei.asa.model;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class MissionTest {

  @Test
  void mission_has_0_executed_days_when_no_worker() {
    var mission = new Mission("mission-code", "Titre", "Description", 10);
    assertEquals(0, mission.executedDays());
  }

  @Test
  void mission_has_non0_executed_days_when_has_executing_worker() {
    var studentContractor = new StudentContractor("student-code", "Lita");
    var mission = new Mission("mission-code", "Titre", "Description", 10);

    studentContractor.execute(
        new DailyMissionExecution(studentContractor, now(), Map.of(mission, 1.)));

    assertEquals(1, mission.executedDays());
  }
}
