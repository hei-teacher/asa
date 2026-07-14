package school.hei.asa.endpoint;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ConverterConfTest {

  private final ConverterConf conf = new ConverterConf();

  @Test
  void numberConverter_creates_bean() {
    var converter = conf.numberConverter();
    assertNotNull(converter);
  }
}
