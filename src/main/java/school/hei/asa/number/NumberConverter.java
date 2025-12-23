package school.hei.asa.number;

import static com.ibm.icu.text.RuleBasedNumberFormat.SPELLOUT;
import static java.util.Locale.FRENCH;

import com.ibm.icu.text.RuleBasedNumberFormat;

public class NumberConverter {
  public String convertToWords(String amount) {
    var formatter = new RuleBasedNumberFormat(FRENCH, SPELLOUT);

    var cleanAmount = amount.replaceAll("[\\u00A0\\u202F\\s]", "").replace(',', '.');

    var parsedAmount = Double.parseDouble(cleanAmount);
    var entier = (long) parsedAmount;
    var result = formatter.format(entier);

    return result.substring(0, 1).toUpperCase() + result.substring(1);
  }
}
