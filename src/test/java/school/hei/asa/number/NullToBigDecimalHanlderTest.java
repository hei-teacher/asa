package school.hei.asa.number;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class NullToBigDecimalHanlderTest {

  @Test
  void toBigDecimalOrZero_with_null_returns_zero() {
    assertEquals(BigDecimal.ZERO, NullToBigDecimalHanlder.toBigDecimalOrZero(null));
  }

  @Test
  void toBigDecimalOrZero_with_value_returns_bigDecimal() {
    assertEquals(BigDecimal.valueOf(100.5), NullToBigDecimalHanlder.toBigDecimalOrZero(100.5));
  }

  @Test
  void toDoubleOrZero_with_null_returns_zero() {
    assertEquals(0.0, NullToBigDecimalHanlder.toDoubleOrZero(null));
  }

  @Test
  void toDoubleOrZero_with_value_returns_value() {
    assertEquals(50.3, NullToBigDecimalHanlder.toDoubleOrZero(50.3));
  }
}
