package school.hei.asa.model;

import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.junit.jupiter.api.Test;

class MissionTest {

  @Test
  void mission_has_0_executed_days_when_no_worker() {
    var mission = new Mission(randomUUID(), "Titre", "Description", 10);
    assertEquals(0, mission.executedDays());
  }

  @Test
  void mission_has_non0_executed_days_when_has_executing_worker() {
    var studentContractor = new StudentContractor(randomUUID(), "Lita");
    var mission = new Mission(randomUUID(), "Titre", "Description", 10);

    studentContractor.execute(mission, Set.of(now()));

    assertEquals(1, mission.executedDays());
  }
}
