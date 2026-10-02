package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class TaxTest {

  private Tax irsa() {
    return new Tax(
        "IRSA",
        "IRSA",
        DeductionType.TAX,
        List.of(
            new TaxProgression(
                0d,
                TaxSide.EMPLOYEE,
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(350000),
                BigDecimal.valueOf(3000)),
            new TaxProgression(
                5d,
                TaxSide.EMPLOYEE,
                BigDecimal.valueOf(350000.01),
                BigDecimal.valueOf(400000),
                BigDecimal.ZERO),
            new TaxProgression(
                10d,
                TaxSide.EMPLOYEE,
                BigDecimal.valueOf(400000.01),
                BigDecimal.valueOf(500000),
                BigDecimal.ZERO),
            new TaxProgression(
                15d,
                TaxSide.EMPLOYEE,
                BigDecimal.valueOf(500000.01),
                BigDecimal.valueOf(600000),
                BigDecimal.ZERO),
            new TaxProgression(
                20d,
                TaxSide.EMPLOYEE,
                BigDecimal.valueOf(600000.01),
                BigDecimal.valueOf(4000000),
                BigDecimal.ZERO),
            new TaxProgression(
                25d,
                TaxSide.EMPLOYEE,
                BigDecimal.valueOf(4000000.01),
                BigDecimal.valueOf(9999999999.99),
                BigDecimal.ZERO)));
  }

  @Test
  void bracket_1_applies_the_3000_floor_when_the_rate_gives_zero() {
    var result = irsa().resolve(BigDecimal.valueOf(250000));

    assertEquals(0, BigDecimal.valueOf(3000).compareTo(result.employeeContributionValue()));
  }

  @Test
  void bracket_1_at_exactly_350000_still_uses_the_floor() {
    var result = irsa().resolve(BigDecimal.valueOf(350000));

    assertEquals(0, BigDecimal.valueOf(3000).compareTo(result.employeeContributionValue()));
  }

  @Test
  void bracket_2_computes_a_real_percentage_above_the_floor() {
    var result = irsa().resolve(BigDecimal.valueOf(380000));

    assertEquals(0, BigDecimal.valueOf(19000.00).compareTo(result.employeeContributionValue()));
  }

  @Test
  void irsa_has_no_employer_side() {
    var result = irsa().resolve(BigDecimal.valueOf(250000));

    assertEquals(0, BigDecimal.ZERO.compareTo(result.employerContributionValue()));
  }

  @Test
  void a_negative_base_is_clamped_to_zero_instead_of_throwing() {
    var result = irsa().resolve(BigDecimal.valueOf(-450000));

    assertEquals(0, BigDecimal.valueOf(3000).compareTo(result.employeeContributionValue()));
  }

  @Test
  void a_base_outside_every_bracket_throws() {
    var tax =
        new Tax(
            "IRSA",
            "IRSA",
            DeductionType.TAX,
            List.of(
                new TaxProgression(
                    0d,
                    TaxSide.EMPLOYEE,
                    BigDecimal.valueOf(0),
                    BigDecimal.valueOf(350000),
                    BigDecimal.valueOf(3000))));

    org.junit.jupiter.api.Assertions.assertThrows(
        RuntimeException.class, () -> tax.resolve(BigDecimal.valueOf(10000000)));
  }
}
