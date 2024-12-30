package school.hei.asa.endpoint.rest.model.th;

import static java.awt.Color.WHITE;
import static java.util.Comparator.comparing;

import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.Accessors;

public class ThYear {
  @Accessors(fluent = true)
  @Getter
  private final int year;

  @Accessors(fluent = true)
  @Getter
  private final String title;

  private final Map<Month, ThMonth> thMonths;
  private final Map<LocalDate, Color> coloredDates;

  @Accessors(fluent = true)
  @Getter
  private final Map<Color, String> colorDescriptions;

  public ThYear(
      int year,
      String title,
      Map<LocalDate, Color> coloredDates,
      Map<Color, String> colorDescriptions) {
    this.year = year;
    this.title = title;
    this.thMonths = thMonths(year);
    this.coloredDates = coloredDates;
    this.colorDescriptions = colorDescriptions;
  }

  private static Map<Month, ThMonth> thMonths(int year) {
    Map<Month, ThMonth> res = new LinkedHashMap<>();
    for (int month = 1; month <= 12; month++) {
      res.put(Month.of(month), new ThMonth(YearMonth.of(year, month)));
    }
    return res;
  }

  public List<ThMonth> months() {
    return thMonths.values().stream().sorted(comparing(ThMonth::yearMonth)).toList();
  }

  public String hexColor(ThMonth thMonth, int day) {
    var yearMonth = thMonth.yearMonth();
    var color =
        thMonth.isFillerDay(day)
            ? WHITE
            : coloredDates.getOrDefault(
                LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day), WHITE);
    return hexColor(color);
  }

  public String hexColor(Color color) {
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
  }
}
