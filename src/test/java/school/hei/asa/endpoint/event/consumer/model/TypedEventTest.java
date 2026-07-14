package school.hei.asa.endpoint.event.consumer.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.UuidCreated;

class TypedEventTest {

  @Test
  void constructor_and_accessors_work() {
    var payload = new UuidCreated("test-uuid");
    var typedEvent = new TypedEvent("school.hei.asa.endpoint.event.model.UuidCreated", payload);

    assertEquals("school.hei.asa.endpoint.event.model.UuidCreated", typedEvent.typeName());
    assertEquals(payload, typedEvent.payload());
  }
}
