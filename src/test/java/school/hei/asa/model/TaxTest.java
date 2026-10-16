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
  void the_floor_still_wins_when_the_cumulative_total_stays_below_it() {
    var result = irsa().resolve(BigDecimal.valueOf(380000));

    assertEquals(0, BigDecimal.valueOf(3000).compareTo(result.employeeContributionValue()));
  }

  @Test
  void every_bracket_is_taxed_at_its_own_rate_instead_of_the_last_one() {
    var result = irsa().resolve(BigDecimal.valueOf(450000));

    assertEquals(0, BigDecimal.valueOf(7500.00).compareTo(result.employeeContributionValue()));
  }

  @Test
  void a_base_spanning_five_brackets_sums_each_slice() {
    var result = irsa().resolve(new BigDecimal("1384374.46"));

    assertEquals(0, new BigDecimal("184374.89").compareTo(result.employeeContributionValue()));
  }

  @Test
  void a_base_above_the_last_bracket_is_taxed_up_to_that_bracket() {
    var tax =
        new Tax(
            "IRSA",
            "IRSA",
            DeductionType.TAX,
            List.of(
                new TaxProgression(
                    10d,
                    TaxSide.EMPLOYEE,
                    BigDecimal.valueOf(0),
                    BigDecimal.valueOf(350000),
                    BigDecimal.ZERO)));

    var result = tax.resolve(BigDecimal.valueOf(10000000));

    assertEquals(0, BigDecimal.valueOf(35000.00).compareTo(result.employeeContributionValue()));
  }

  @Test
  void a_single_bracket_tax_is_unchanged_by_the_cumulative_computation() {
    var cnaps =
        new Tax(
            "CNAPS",
            "CNAPS",
            DeductionType.DEDUCTION,
            List.of(
                new TaxProgression(
                    1d,
                    TaxSide.EMPLOYEE,
                    BigDecimal.valueOf(0),
                    new BigDecimal("9999999999.99"),
                    BigDecimal.ZERO),
                new TaxProgression(
                    13d,
                    TaxSide.EMPLOYER,
                    BigDecimal.valueOf(0),
                    new BigDecimal("9999999999.99"),
                    BigDecimal.ZERO)));

    var result = cnaps.resolve(BigDecimal.valueOf(1412627));

    assertEquals(0, new BigDecimal("14126.27").compareTo(result.employeeContributionValue()));
    assertEquals(0, new BigDecimal("183641.51").compareTo(result.employerContributionValue()));
  }

  @Test
  void a_reduction_is_subtracted_from_the_total_but_never_below_the_floor() {
    var result = irsa().resolve(BigDecimal.valueOf(450000), BigDecimal.valueOf(2000));

    assertEquals(0, BigDecimal.valueOf(5500.00).compareTo(result.employeeContributionValue()));
  }

  @Test
  void a_reduction_larger_than_the_tax_leaves_the_floor_standing() {
    var result = irsa().resolve(BigDecimal.valueOf(450000), BigDecimal.valueOf(20000));

    assertEquals(0, BigDecimal.valueOf(3000).compareTo(result.employeeContributionValue()));
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
}
