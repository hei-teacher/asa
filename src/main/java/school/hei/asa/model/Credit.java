package school.hei.asa.model;

import static school.hei.asa.number.NullToBigDecimalHanlder.calculatePercentageValue;

import java.math.BigDecimal;
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
    return calculatePercentageValue(rate, base);
  }
}
