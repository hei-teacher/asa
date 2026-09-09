package school.hei.asa.model;

import static school.hei.asa.number.NullToBigDecimalHanlder.calculatePercentageValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Credit {
  private String id;
  private String name;
  private Double divisor;
  private Double rate;
  private Double numberOfUnits;

  public BigDecimal getAmount(BigDecimal base) {
    var perUnit =
        divisor == null ? base : base.divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP);
    var units = numberOfUnits == null ? BigDecimal.ZERO : BigDecimal.valueOf(numberOfUnits);
    return calculatePercentageValue(rate, perUnit).multiply(units);
  }
}
