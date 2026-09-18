package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class EarnedCreditTest {

  @Test
  void amount_is_base_divided_by_divisor_times_rate_times_units() {
    var credit = new Credit("ABSENCE", "ABSENCE", "Absence", 24d, 100d, true);
    var earnedCredit = new EarnedCredit("id", credit, 2d, YearMonth.of(2026, 4), null);

    var result = earnedCredit.getAmount(BigDecimal.valueOf(450000));

    assertEquals(0, BigDecimal.valueOf(37500.00).compareTo(result));
  }

  @Test
  void null_divisor_uses_the_base_directly() {
    var credit = new Credit("TEST_NEGATIVE", "TEST_NEGATIVE", "Retenue de test", null, -100d, true);
    var earnedCredit = new EarnedCredit("id", credit, 2d, YearMonth.of(2026, 5), null);

    var result = earnedCredit.getAmount(BigDecimal.valueOf(450000));

    assertEquals(0, BigDecimal.valueOf(-900000.00).compareTo(result));
  }

  @Test
  void null_number_of_units_gives_a_zero_amount() {
    var credit = new Credit("ABSENCE", "ABSENCE", "Absence", 24d, 100d, true);
    var earnedCredit = new EarnedCredit("id", credit, null, YearMonth.of(2026, 4), null);

    var result = earnedCredit.getAmount(BigDecimal.valueOf(450000));

    assertEquals(0, BigDecimal.ZERO.compareTo(result));
  }

  @Test
  void a_neutral_zero_rate_credit_gives_a_zero_amount_regardless_of_units() {
    var credit = new Credit("INDEMNITES_DIVERSES", "INDEMNITES_DIVERSES", "Indemnites diverses", 1d, 0d, true);
    var earnedCredit = new EarnedCredit("id", credit, 5d, YearMonth.of(2026, 4), null);

    var result = earnedCredit.getAmount(BigDecimal.valueOf(450000));

    assertEquals(0, BigDecimal.ZERO.compareTo(result));
  }
}
