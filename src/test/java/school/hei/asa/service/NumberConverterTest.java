package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import school.hei.asa.number.NumberConverter;

public class NumberConverterTest {

  @Test
  void amount_with_currency() {
    var numberConverter = new NumberConverter();
    var amount = "180 000";
    assertEquals("Cent quatre-vingt mille", numberConverter.convertToWords(amount));
  }

  @Test
  void large_amount_with_currency() {
    var numberConverter = new NumberConverter();
    var amount = "2 500 000";
    assertEquals("Deux millions cinq cent mille", numberConverter.convertToWords(amount));
  }

  @Test
  void amount_without_currency() {
    var numberConverter = new NumberConverter();
    var amount = "1000";
    assertEquals("Mille", numberConverter.convertToWords(amount));
  }

  @Test
  void amount_with_decimal() {
    var numberConverter = new NumberConverter();
    var amount = "1101,25";
    assertEquals("Mille cent un", numberConverter.convertToWords(amount));
  }
}
