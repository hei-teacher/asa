package school.hei.asa.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import school.hei.asa.number.NumberFormatter;

class NumberFormatTest {

  private NumberFormatter subject;

  @BeforeEach
  void setUp() {
    subject = new NumberFormatter();
  }

  @ParameterizedTest
  @CsvSource({
    "0, '0'",
    "450, '450'",
    "-450, '-450'",
    "1000, '1.0K'",
    "1500, '1.5K'",
    "-85400, '-85.4K'",
    "1000000, '1.00M'",
    "2350000, '2.35M'",
    "-5000000, '-5.00M'",
    "1000000000, '1.00B'",
    "12340000000, '12.34B'",
    "-9876000000, '-9.88B'"
  })
  void compact_double_should_format_correctly_according_to_scale(double input, String expected) {
    String actual = subject.compact(input).replace(',', '.');
    assertEquals(expected, actual);
  }

  @Test
  void compact_string_should_parse_and_format_correctly() {
    assertEquals("550.0K", subject.compact("550000.0").replace(',', '.'));
    assertEquals("-2.0K", subject.compact("-2000.0").replace(',', '.'));
    assertEquals("0", subject.compact("0.0"));
  }
}
