package school.hei.asa.model;

import static school.hei.asa.model.TaxSide.EMPLOYEE;
import static school.hei.asa.model.TaxSide.EMPLOYER;
import static school.hei.asa.number.NullToBigDecimalHanlder.calculatePercentageValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
    return new TaxAmount(resolveSide(base, EMPLOYER), resolveSide(base, EMPLOYEE));
  }

  private BigDecimal resolveSide(BigDecimal base, TaxSide side) {
    var bracketsForSide =
        taxProgressions.stream().filter(range -> range.taxSide() == side).toList();
    if (bracketsForSide.isEmpty()) {
      return BigDecimal.ZERO;
    }
    var nonNegativeBase = base.max(BigDecimal.ZERO);
    return findBracket(bracketsForSide, nonNegativeBase)
        .map(
            bracket ->
                calculatePercentageValue(bracket.rate(), nonNegativeBase)
                    .max(bracket.defaultValue()))
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("The number %s is not supported", nonNegativeBase)));
  }

  private Optional<TaxProgression> findBracket(
      List<TaxProgression> bracketsForSide, BigDecimal base) {
    return bracketsForSide.stream()
        .filter(
            range ->
                range.minAmount().compareTo(base) <= 0 && base.compareTo(range.maxAmount()) <= 0)
        .findFirst();
  }
}
