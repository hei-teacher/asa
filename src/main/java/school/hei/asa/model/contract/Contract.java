package school.hei.asa.model.contract;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
  public LocalDate entranceDate() {
    return LocalDate.ofInstant(entranceInstant, ZoneId.systemDefault());
  }

  /** Last day covered by the contract (inclusive), or null if open-ended. */
  public LocalDate endDate() {
    return endInstant == null ? null : LocalDate.ofInstant(endInstant, ZoneId.systemDefault());
  }

  /** {@code date}, capped to the contract's end date. */
  public LocalDate capToEnd(LocalDate date) {
    var endDate = endDate();
    return endDate == null || date.isBefore(endDate) ? date : endDate;
  }

  /** Number of days of [from, to] (inclusive) covered by the contract. */
  public long daysCovered(LocalDate from, LocalDate to) {
    var start = from.isAfter(entranceDate()) ? from : entranceDate();
    var end = capToEnd(to);
    return start.isAfter(end) ? 0 : ChronoUnit.DAYS.between(start, end) + 1;
  }

  public boolean overlaps(YearMonth yearMonth) {
    var startsBeforeMonthEnds = !entranceDate().isAfter(yearMonth.atEndOfMonth());
    var endsAfterMonthStarts = endDate() == null || !endDate().isBefore(yearMonth.atDay(1));
    return startsBeforeMonthEnds && endsAfterMonthStarts;
  }

  public String ppId() {
    return String.format("%s_%s_%s_%s", worker.code(), worker.name(), level, entranceInstant);
  }
}
