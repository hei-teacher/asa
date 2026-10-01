package school.hei.asa.number;

import java.math.BigDecimal;

public class NullToBigDecimalHanlder {
  public static BigDecimal toBigDecimalOrZero(Double value) {
    return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
  }

  public static double toDoubleOrZero(Double value) {
    return value == null ? 0 : value;
  }
}
