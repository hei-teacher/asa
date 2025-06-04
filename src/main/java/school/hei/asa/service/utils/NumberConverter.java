package school.hei.asa.service.utils;

import static com.ibm.icu.text.RuleBasedNumberFormat.SPELLOUT;
import static java.util.Locale.FRENCH;

import com.ibm.icu.text.RuleBasedNumberFormat;

public class NumberConverter {
  public String convertToWords(String amount) {
    var formatter = new RuleBasedNumberFormat(FRENCH, SPELLOUT);

    String numericOnly = amount.replaceAll("\\D", "");
    var parsedAmount = Long.parseLong(numericOnly);
    String result = formatter.format(parsedAmount);

    return result.substring(0, 1).toUpperCase() + result.substring(1);
  }
}
