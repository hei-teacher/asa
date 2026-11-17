package school.hei.asa.model.contract;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import school.hei.asa.model.Worker;

public record Contract(
    Worker worker,
    String jobTitle,
    ContractLevel level,
    Instant entranceInstant,
    Instant endInstant,
    Duration duration,
    String company,
    String contractBucketKey) {
  public boolean overlaps(YearMonth yearMonth) {
    var zone = ZoneId.systemDefault();
    var startsBeforeMonthEnds =
        !LocalDate.ofInstant(entranceInstant, zone).isAfter(yearMonth.atEndOfMonth());
    var endsAfterMonthStarts =
        endInstant == null || !LocalDate.ofInstant(endInstant, zone).isBefore(yearMonth.atDay(1));
    return startsBeforeMonthEnds && endsAfterMonthStarts;
  }

  public String ppId() {
    return String.format("%s_%s_%s_%s", worker.code(), worker.name(), level, entranceInstant);
  }
}
