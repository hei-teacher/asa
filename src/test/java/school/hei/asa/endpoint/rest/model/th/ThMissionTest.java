package school.hei.asa.endpoint.rest.model.th;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ThMissionTest {

  @Test
  void can_calculate_executed_days() {
    var execution1 =
        new ThMissionExecution("me-code", "wc1", LocalDate.now(), 0.5, "comment1", true);
    var execution2 =
        new ThMissionExecution("me-code", "wc2", LocalDate.now(), 0.3, "comment2", true);
    var thMission =
        new ThMission("mcode", "mtitle", "mdesc", List.of(execution1, execution2), true);

    assertEquals(0.8, thMission.executedDays());
  }

  @Test
  void can_filter_by_worker_code() {
    var execution1 =
        new ThMissionExecution("me-code", "wc1", LocalDate.now(), 0.2, "comment1", true);
    var execution2 =
        new ThMissionExecution("me-code", "wc2", LocalDate.now(), 0.2, "comment2", true);
    var execution3 =
        new ThMissionExecution("me-code", "wc1", LocalDate.now(), 0.3, "comment3", true);
    var thMission =
        new ThMission(
            "mcode", "mtitle", "mdesc", List.of(execution1, execution2, execution3), true);

    var filteredMission = thMission.filterByWorkerCode("wc1");

    assertEquals(2, filteredMission.getMissionExecutions().size());
    assertEquals(0.5, filteredMission.executedDays());
  }
}
