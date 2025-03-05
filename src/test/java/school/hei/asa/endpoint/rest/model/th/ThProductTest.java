package school.hei.asa.endpoint.rest.model.th;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
