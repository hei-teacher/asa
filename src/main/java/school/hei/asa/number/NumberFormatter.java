package school.hei.asa.number;

import org.springframework.stereotype.Component;

@Component("numberFormatter")
public class NumberFormatter {

  public String compact(String rawAmount) {
    double value = Double.parseDouble(rawAmount);
    return compact(value);
  }

  public String compact(double value) {
    double absValue = Math.abs(value);
    String sign = value < 0 ? "-" : "";

    if (absValue >= 1_000_000_000) {
      return sign + String.format("%.2fB", absValue / 1_000_000_000);
    }
    if (absValue >= 1_000_000) {
      return sign + String.format("%.2fM", absValue / 1_000_000);
    }
    if (absValue >= 1_000) {
      return sign + String.format("%.1fK", absValue / 1_000);
    }
    return sign + String.format("%.0f", absValue);
  }
}
