package school.hei.asa.endpoint.rest.model.th;

import static java.time.Month.DECEMBER;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.Map;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.Mission;

class ThYearTest {

  @Test
  void december_2024() {
    var year =
        new ThYear(
            2024,
            "title",
            Map.of(),
            Map.of(),
            Map.of(DECEMBER, Map.of(Mission.Type.unpaidCare, 2.0)),
            Map.of(DECEMBER, 10L));
    assertTrue(
        year.months().contains(new ThMonth(YearMonth.of(2024, DECEMBER), 2.0, 0.0, 0.0, 10L)));
  }
}
