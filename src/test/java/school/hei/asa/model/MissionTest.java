package school.hei.asa.model;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class MissionTest {

  @Test
  void mission_has_0_executed_days_when_no_worker() {
    var product = new Product("pcode", "pname", "pdescription");
    var mission = new Mission("mission-code", "Titre", "Description", 10, product);
    assertEquals(0, mission.executedDays());
  }

  @Test
  void mission_has_non0_executed_days_when_has_executing_worker() {
    var product = new Product("pcode", "pname", "pdescription");
    var studentContractor = new StudentContractor("student-code", "Lita");
    var mission = new Mission("mission-code", "Titre", "Description", 10, product);

    studentContractor.execute(
        new DailyExecution(
            studentContractor,
            now(),
            List.of(new MissionExecution(mission, studentContractor, now(), 1.))));

    assertEquals(1, mission.executedDays());
  }
}
