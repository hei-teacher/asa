package school.hei.asa.model.contract;

import static java.time.ZoneId.systemDefault;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.hei.asa.model.contract.ContractType.fullTimeEmployee;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.Worker;

class ContractTest {

  @Test
  void open_ended_contract_overlaps_every_month_from_its_start() {
    var contract = contract(LocalDate.of(2026, 3, 15), null);

    assertFalse(contract.overlaps(YearMonth.of(2026, 2)));
    assertTrue(contract.overlaps(YearMonth.of(2026, 3)));
    assertTrue(contract.overlaps(YearMonth.of(2030, 12)));
  }

  @Test
  void ended_contract_overlaps_only_months_until_its_end() {
    var contract = contract(LocalDate.of(2021, 11, 1), LocalDate.of(2025, 12, 31));

    assertFalse(contract.overlaps(YearMonth.of(2021, 10)));
    assertTrue(contract.overlaps(YearMonth.of(2021, 11)));
    assertTrue(contract.overlaps(YearMonth.of(2025, 12)));
    assertFalse(contract.overlaps(YearMonth.of(2026, 1)));
  }

  private static Contract contract(LocalDate start, LocalDate end) {
    return new Contract(
        new Worker(
            "W-1", "name", "email", "fullname", "address", "city", "nif", "stat", null, null),
        "jobTitle",
        new ContractLevel("L", fullTimeEmployee, 1_000d, null, null),
        instant(start),
        end == null ? null : instant(end),
        null,
        "company",
        "contractBucketKey");
  }

  private static Instant instant(LocalDate date) {
    return date.atStartOfDay(systemDefault()).toInstant();
  }
}
