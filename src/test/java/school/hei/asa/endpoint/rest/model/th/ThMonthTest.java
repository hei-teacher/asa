package school.hei.asa.endpoint.rest.model.th;

import static java.time.Month.DECEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.asa.endpoint.rest.model.th.ThMonth.FILLER_DAY;

import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;

class ThMonthTest {

  @Test
  void december() {
    var december = new ThMonth(YearMonth.of(2024, DECEMBER));
    var weeks = december.weeks();

    var week48 = weeks.getFirst();
    assertEquals(48, week48);
    assertEquals(
        List.of(FILLER_DAY, FILLER_DAY, FILLER_DAY, FILLER_DAY, FILLER_DAY, FILLER_DAY, 1),
        december.daysByWeek(week48));

    var week50 = weeks.get(2);
    assertEquals(50, week50);
    assertEquals(List.of(9, 10, 11, 12, 13, 14, 15), december.daysByWeek(week50));

    var week53 = weeks.getLast();
    assertEquals(53, week53);
    assertEquals(List.of(30, 31), december.daysByWeek(week53));
  }
}
