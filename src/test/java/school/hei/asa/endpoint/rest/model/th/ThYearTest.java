package school.hei.asa.endpoint.rest.model.th;

import static java.time.Month.DECEMBER;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class ThYearTest {

  @Test
  void december_2024() {
    var year = new ThYear(2024);
    assertTrue(year.months().contains(new ThMonth(YearMonth.of(2024, DECEMBER))));
  }
}
