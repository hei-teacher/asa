package school.hei.asa.service.utils;

import com.ibm.icu.text.RuleBasedNumberFormat;

import java.util.Locale;

public class ToWords {
    public String convertToWords(String amount) {
        var formatter = new RuleBasedNumberFormat(Locale.FRENCH, RuleBasedNumberFormat.SPELLOUT);

        String numericOnly = amount.replaceAll("\\D", "");
        var parsedAmount = Long.parseLong(numericOnly);
        String result = formatter.format(parsedAmount);

        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }
}
