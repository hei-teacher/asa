package school.hei.asa.endpoint.rest.model.th;

import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.Mission;

class ThYearTest {

  @Test
  void december_2024() {
    var december22 = LocalDate.of(2024, DECEMBER, 22);
    var year =
        new ThYear(
            2024,
            "title",
            Map.of(),
            Map.of(),
            Map.of(DECEMBER, Map.of(Mission.Type.unpaidCare, 2.0)),
            Map.of(DECEMBER, List.of(december22)));
    assertTrue(
        year.months()
            .contains(
                new ThMonth(YearMonth.of(2024, DECEMBER), 2.0, 0.0, 0.0, List.of(december22))));
  }

  @Test
  void hexColor_with_filler_day_returns_white() {
    var year = new ThYear(2024, "title", Map.of(), Map.of(), Map.of(), Map.of());
    var january = year.months().getFirst();
    assertEquals("#ffffff", year.hexColor(january, ThMonth.FILLER_DAY));
  }

  @Test
  void hexColor_with_colored_date_returns_correct_hex() {
    var coloredDate = LocalDate.of(2024, JANUARY, 15);
    var year =
        new ThYear(
            2024,
            "title",
            Map.of(coloredDate, Color.RED),
            Map.of(),
            Map.of(JANUARY, Map.of(Mission.Type.work, 1.0)),
            Map.of());
    var january = year.months().getFirst();
    assertEquals("#ff0000", year.hexColor(january, 15));
  }

  @Test
  void hexColor_with_color_returns_formatted_hex() {
    var year = new ThYear(2024, "title", Map.of(), Map.of(), Map.of(), Map.of());
    assertEquals("#000000", year.hexColor(Color.BLACK));
    assertEquals("#ffffff", year.hexColor(Color.WHITE));
    assertEquals("#ff0000", year.hexColor(Color.RED));
    assertEquals("#0000ff", year.hexColor(Color.BLUE));
    assertEquals("#00ff00", year.hexColor(Color.GREEN));
  }

  @Test
  void months_returns_sorted_result() {
    var year =
        new ThYear(
            2024,
            "title",
            Map.of(),
            Map.of(),
            Map.of(
                DECEMBER, Map.of(Mission.Type.work, 1.0), JANUARY, Map.of(Mission.Type.work, 2.0)),
            Map.of());
    var months = year.months();
    assertEquals(12, months.size());
    assertEquals(YearMonth.of(2024, JANUARY), months.get(0).yearMonth());
    assertEquals(YearMonth.of(2024, DECEMBER), months.get(11).yearMonth());
  }

  @Test
  void constructor_with_empty_maps_does_not_throw() {
    var year = new ThYear(2024, "title", Map.of(), Map.of(), Map.of(), Map.of());
    assertEquals(2024, year.year());
    assertEquals("title", year.title());
    assertEquals(12, year.months().size());
  }
}
