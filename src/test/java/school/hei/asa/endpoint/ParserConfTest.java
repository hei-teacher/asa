package school.hei.asa.endpoint;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ParserConfTest {

  private final ParserConf conf = new ParserConf();

  @Test
  void numberParser_creates_bean() {
    var parser = conf.numberParser();
    assertNotNull(parser);
  }
}
