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
  private final DeductionType deductionType;
  private final List<TaxProgression> taxProgressions;

  public TaxAmount resolve(BigDecimal base) {
    return resolve(base, BigDecimal.ZERO);
  }

  public TaxAmount resolve(BigDecimal base, BigDecimal employeeReduction) {
    return new TaxAmount(
        resolveSide(base, EMPLOYER, BigDecimal.ZERO),
        resolveSide(base, EMPLOYEE, employeeReduction));
  }

  private BigDecimal resolveSide(BigDecimal base, TaxSide side, BigDecimal reduction) {
    var bracketsForSide =
        taxProgressions.stream().filter(range -> range.taxSide() == side).toList();
    if (bracketsForSide.isEmpty()) {
      return BigDecimal.ZERO;
    }
    var nonNegativeBase = base.max(BigDecimal.ZERO);
    var total = BigDecimal.ZERO;
    var floor = BigDecimal.ZERO;
    for (var bracket : bracketsForSide) {
      if (bracket.minAmount().compareTo(nonNegativeBase) > 0) {
        continue;
      }
      var taxableSlice =
          bracket
              .maxAmount()
              .min(nonNegativeBase)
              .subtract(bracket.minAmount())
              .max(BigDecimal.ZERO);
      total = total.add(calculatePercentageValue(bracket.rate(), taxableSlice));
      floor = floor.max(bracket.defaultValue());
    }
    return total.subtract(reduction).max(floor);
  }
}
