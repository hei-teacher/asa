package school.hei.asa.model;

import static school.hei.asa.model.TaxSide.EMPLOYEE;
import static school.hei.asa.model.TaxSide.EMPLOYER;
import static school.hei.asa.number.NullToBigDecimalHanlder.calculatePercentageValue;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Tax {
  private final String id;
  private final String name;
  private final List<TaxProgression> taxProgression;

  public TaxAmount resolve(BigDecimal base) {
    var rateEmployee = findRate(base, EMPLOYEE);
    var rateEmployer = findRate(base, EMPLOYER);
    return new TaxAmount(
        calculatePercentageValue(rateEmployer, base), calculatePercentageValue(rateEmployee, base));
  }

  private Double findRate(BigDecimal base, TaxSide side) {
    return taxProgression.stream()
        .filter(
            range ->
                range.minAmount().compareTo(base) <= 0
                    && base.compareTo(range.maxAmount()) <= 0
                    && range.taxSide() == side)
        .findFirst()
        .orElseThrow(
            () -> new RuntimeException(String.format("The number %s is not supported", base)))
        .rate();
  }
}
