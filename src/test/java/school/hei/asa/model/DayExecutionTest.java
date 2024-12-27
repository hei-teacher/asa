package school.hei.asa.model;

import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;

class DayExecutionTest {

  @Test
  void missionPercentagesSum_lt1_isIllegal() {
    var worker = new PartnerContractor(randomUUID(), "name");
    var mission = new Mission(randomUUID(), "title", "description", 10);
    assertThrows(
        IllegalArgumentException.class,
        () -> new DayExecution(worker, now(), Map.of(mission, 0.2)));
  }
}
