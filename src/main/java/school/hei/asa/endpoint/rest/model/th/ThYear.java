package school.hei.asa.endpoint.rest.model.th;

import java.time.Month;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ThYear {
  private final int year;
  private final Map<Month, ThMonth> thMonths;

  public ThYear(int year) {
    this.year = year;
    this.thMonths = thMonths(year);
  }

  private static Map<Month, ThMonth> thMonths(int year) {
    Map<Month, ThMonth> res = new LinkedHashMap<>();
    for (int month = 1; month <= 12; month++) {
      res.put(Month.of(month), new ThMonth(YearMonth.of(year, month)));
    }
    return res;
  }

  public List<ThMonth> months() {
    return thMonths.values().stream().sorted(Comparator.comparing(ThMonth::yearMonth)).toList();
  }
}
