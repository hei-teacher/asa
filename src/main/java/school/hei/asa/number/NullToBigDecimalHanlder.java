package school.hei.asa.number;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NullToBigDecimalHanlder {
  public static BigDecimal toBigDecimalOrZero(Double value) {
    return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
  }

  public static double toDoubleOrZero(Double value) {
    return value == null ? 0 : value;
  }

  public static BigDecimal calculatePercentageValue(Double percentage, BigDecimal base) {
    return base.multiply(toBigDecimalOrZero(percentage))
        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
  }
}
