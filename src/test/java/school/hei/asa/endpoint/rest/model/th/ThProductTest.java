package school.hei.asa.endpoint.rest.model.th;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;

class ThProductTest {
  @Test
  void can_filter_by_worker_code() {
    var execution1 = new ThMissionExecution("mcode", "wc1", now(), 0.1, "ecomment1", true, true);
    var execution2 = new ThMissionExecution("mcode", "wc2", now(), 0.2, "ecomment2", true, true);
    var thMission =
        new ThMission("mcode", "mtitle", "mdesc", List.of(execution1, execution2), true);
    var thProduct = new ThProduct("pcode", "pname", "pdesc", List.of(thMission), true);

    assertEquals(0.2, thProduct.filterByWorkerCode("wc2").executedDays());
  }

  @Test
  void can_filter_by_month() {
    var execution1 =
        new ThMissionExecution(
            "mcode", "wc1", LocalDate.of(2025, 3, 1), 0.1, "ecomment1", true, true);
    var execution2 =
        new ThMissionExecution(
            "mcode", "wc2", LocalDate.of(2025, 1, 1), 0.2, "ecomment2", true, true);
    var thMission =
        new ThMission("mcode", "mtitle", "mdesc", List.of(execution1, execution2), true);

    var thProduct = new ThProduct("pcode", "pname", "pdesc", List.of(thMission), true);

    assertNotEquals(thProduct, thProduct.filterByMonth(Month.MARCH));
  }

  @Test
  void studentExecutedDays_is_less_than_executedDays() {
    var execution1 =
        new ThMissionExecution(
            "mcode", "wc1", LocalDate.of(2025, 3, 1), 0.1, "ecomment1", true, false);
    var execution2 =
        new ThMissionExecution(
            "mcode", "wc2", LocalDate.of(2025, 1, 1), 0.2, "ecomment2", true, true);
    var thMission =
        new ThMission("mcode", "mtitle", "mdesc", List.of(execution1, execution2), true);

    var thProduct = new ThProduct("pcode", "pname", "pdesc", List.of(thMission), true);
    assertTrue(thProduct.studentExecutedDays() < thProduct.executedDays());
  }
}
