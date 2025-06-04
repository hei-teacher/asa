package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import school.hei.asa.service.utils.NumberConverter;

public class NumberConverterTest {

  @Test
  void amount_with_currency() {
    var numberConverter = new NumberConverter();
    var amount = "180 000 Ar";
    assertEquals("Cent quatre-vingt mille", numberConverter.convertToWords(amount));
  }

  @Test
  void large_amount_with_currency() {
    var numberConverter = new NumberConverter();
    var amount = "2 500 000 Ar";
    assertEquals("Deux millions cinq cent mille", numberConverter.convertToWords(amount));
  }

  @Test
  void amount_without_currency() {
    var numberConverter = new NumberConverter();
    var amount = "1000";
    assertEquals("Mille", numberConverter.convertToWords(amount));
  }
}
