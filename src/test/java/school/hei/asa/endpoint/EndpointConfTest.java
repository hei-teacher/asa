package school.hei.asa.endpoint;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EndpointConfTest {

  private final EndpointConf conf = new EndpointConf();

  @Test
  void objectMapper_creates_bean() {
    var mapper = conf.objectMapper();
    assertNotNull(mapper);
    assertFalse(
        mapper.isEnabled(
            com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    assertFalse(
        mapper.isEnabled(
            com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
  }
}
