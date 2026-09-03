package school.hei.asa.model;

import static school.hei.asa.model.TaxSide.EMPLOYEE;
import static school.hei.asa.model.TaxSide.EMPLOYER;
import static school.hei.asa.number.NullToBigDecimalHanlder.calculatePercentageValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ProgressiveTax extends Tax {
  private final List<TaxProgression> taxProgression;

  public ProgressiveTax(String id, String name, List<TaxProgression> taxProgression) {
    super(id, name);
    this.taxProgression = taxProgression;
  }

  @Override
  public TaxAmount resolve(BigDecimal base) {
    var rateEmployee =
        Objects.requireNonNull(
                taxProgression.stream()
                    .filter(
                        range ->
                            range.minAmount().compareTo(base) <= 0
                                && base.compareTo(range.maxAmount()) <= 0
                                && range.taxSide() == EMPLOYEE)
                    .findFirst()
                    .orElseThrow(
                        () ->
                            new RuntimeException(
                                String.format("The number %s is not supported", base))))
            .rate();

    var rateEmployer =
        Objects.requireNonNull(
                taxProgression.stream()
                    .filter(
                        range ->
                            range.minAmount().compareTo(base) <= 0
                                && base.compareTo(range.maxAmount()) <= 0
                                && range.taxSide() == EMPLOYER)
                    .findFirst()
                    .orElseThrow(
                        () ->
                            new RuntimeException(
                                String.format("The number %s is not supported", base))))
            .rate();
    return new TaxAmount(
        calculatePercentageValue(rateEmployer, base), calculatePercentageValue(rateEmployee, base));
  }
}
