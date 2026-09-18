package school.hei.asa.model;

import static school.hei.asa.number.NullToBigDecimalHanlder.calculatePercentageValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EarnedCredit {
  private String id;
  private Credit credit;
  private Double numberOfUnits;
  private YearMonth yearMonth;
  private Worker worker;

  public BigDecimal getAmount(BigDecimal base) {
    var perUnit =
        credit.getDivisor() == null
            ? base
            : base.divide(BigDecimal.valueOf(credit.getDivisor()), 2, RoundingMode.HALF_UP);
    var units = numberOfUnits == null ? BigDecimal.ZERO : BigDecimal.valueOf(numberOfUnits);
    return calculatePercentageValue(credit.getRate(), perUnit).multiply(units);
  }
}
