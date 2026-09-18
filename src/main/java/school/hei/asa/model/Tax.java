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

  // default_value is a floor/fixed amount for brackets a rate alone can't express
  // (ex: IRSA's 0-350k bracket is 0% but has a 3 000 Ar minimum; a fully fixed tax
  // would be rate=0 with default_value = its flat amount).
  private BigDecimal resolveSide(BigDecimal base, TaxSide side) {
    var bracketsForSide =
        taxProgressions.stream().filter(range -> range.taxSide() == side).toList();
    if (bracketsForSide.isEmpty()) {
      return BigDecimal.ZERO;
    }
    // une base imposable ne peut pas etre negative (ex: une retenue diverse plus grosse que le
    // salaire) : on la ramene a 0 plutot que de laisser la recherche de tranche echouer.
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
