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
import java.util.stream.Collectors;

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
          Map<Color, String> colorDescriptions,
          Map<Month, Map<String, Integer>> missionCounts) {
    this.year = year;
    this.title = title;
    this.thMonths = thMonths(year, mapMissionCountsToDescriptions(missionCounts));
    this.coloredDates = coloredDates;
    this.colorDescriptions = colorDescriptions;
  }

  private static Map<Month, ThMonth> thMonths(int year, Map<Month, String> descriptions) {
    Map<Month, ThMonth> res = new LinkedHashMap<>();
    for (int month = 1; month <= 12; month++) {
      ThMonth thMonth = new ThMonth(YearMonth.of(year, month));
      if (descriptions.containsKey(Month.of(month))) {
        thMonth.setDescription(descriptions.get(Month.of(month)));
      }
      res.put(Month.of(month), thMonth);
    }
    return res;
  }

  private static Map<Month, String> mapMissionCountsToDescriptions(
          Map<Month, Map<String, Integer>> missionCounts) {
    Map<Month, String> descriptions = new LinkedHashMap<>();

    Map<String, String> missionTypeMapping = Map.of(
            "unpaidCare", "UnpaidCareDays",
            "work", "WorkDays",
            "paidCare", "PaidCareDays"
    );

    missionCounts.forEach((month, counts) -> {
      String description = counts.entrySet().stream()
              .map(entry -> missionTypeMapping.getOrDefault(entry.getKey(), entry.getKey()) + ": " + entry.getValue())
              .collect(Collectors.joining(", "));
      descriptions.put(month, description);
    });

    return descriptions;
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

