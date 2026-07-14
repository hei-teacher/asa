package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import school.hei.asa.number.NumberParser;

public class NumberParserTest {

  @Test
  void number_to_double() {
    var numberParser = new NumberParser();
    var amount = "180000,905";
    assertEquals(180000.905, numberParser.parseToDouble(amount));
  }

  @Test
  void double_to_number_with_space() {
    var numberParser = new NumberParser();
    var amount = BigDecimal.valueOf(1800000.905);
    assertEquals("1 800 001", numberParser.parseToNumber(amount));
  }

  @Test
  void double_to_number_with_space_and_decimal() {
    var numberParser = new NumberParser();
    var amount = BigDecimal.valueOf(1800000.15);
    assertEquals("1 800 000", numberParser.parseToNumber(amount));
  }

  @Test
  void parseToDouble_with_null_returns_zero() {
    var numberParser = new NumberParser();
    assertEquals(0.0, numberParser.parseToDouble(null));
  }

  @Test
  void parseToDouble_with_blank_returns_zero() {
    var numberParser = new NumberParser();
    assertEquals(0.0, numberParser.parseToDouble("   "));
  }

  @Test
  void parseToDouble_with_invalid_format_throws() {
    var numberParser = new NumberParser();
    assertThrows(IllegalArgumentException.class, () -> numberParser.parseToDouble("not-a-number"));
  }

  @Test
  void parseToNumber_with_null_returns_empty() {
    var numberParser = new NumberParser();
    assertEquals("", numberParser.parseToNumber(null));
  }
}
