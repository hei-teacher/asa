package school.hei.asa.service;

import org.junit.jupiter.api.Test;
import school.hei.asa.service.utils.ToWords;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToWordsTest {

    @Test
    void amount_with_currency() {
        var toWords = new ToWords();
        var amount = "180 000 Ar";
        assertEquals("Cent quatre-vingt mille", toWords.convertToWords(amount));
    }

    @Test
    void large_amount_with_currency() {
        var toWords = new ToWords();
        var amount = "2 500 000 Ar";
        assertEquals("Deux millions cinq cent mille", toWords.convertToWords(amount));
    }

    @Test
    void amount_without_currency() {
        var toWords = new ToWords();
        var amount = "1000";
        assertEquals("Mille", toWords.convertToWords(amount));
    }
}
