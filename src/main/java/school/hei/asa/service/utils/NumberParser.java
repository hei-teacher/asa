package school.hei.asa.service.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberParser {
  public double parseToDouble(String amount) {
    if (amount == null || amount.isBlank()) {
      return 0.0;
    }

    try {
      String parsed =
          amount.replaceAll("[^\\d,\\.]", "").replaceAll("\\s+", "").replaceAll(",", ".");

      return Double.parseDouble(parsed);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Incorrect amount format : " + amount);
    }
  }

  public String parseToNumber(double number) {
    NumberFormat formatter = NumberFormat.getInstance(Locale.FRANCE);
    formatter.setMinimumFractionDigits(0);
    String formatted = formatter.format(number);
    return formatted.replaceAll("[\\u00A0\\u202F]", " ");
  }
}
