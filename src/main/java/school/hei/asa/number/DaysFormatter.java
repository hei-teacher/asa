package school.hei.asa.number;

import java.math.BigDecimal;

public final class DaysFormatter {

  private DaysFormatter() {}

  public static String format(double days) {
    var normalized = BigDecimal.valueOf(days).stripTrailingZeros();
    if (normalized.scale() <= 0) {
      return normalized.toBigInteger().toString();
    }
    return normalized.toPlainString();
  }
}
