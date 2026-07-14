package school.hei.asa.endpoint.event.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class UuidCreatedTest {

  @Test
  void constructor_sets_uuid() {
    var event = new UuidCreated("test-uuid");
    assertEquals("test-uuid", event.getUuid());
  }

  @Test
  void builder_works() {
    var event = UuidCreated.builder().uuid("test-uuid").build();
    assertEquals("test-uuid", event.getUuid());
  }

  @Test
  void maxConsumerDuration_returns_10_seconds() {
    var event = new UuidCreated("test-uuid");
    assertEquals(Duration.ofSeconds(10), event.maxConsumerDuration());
  }

  @Test
  void maxConsumerBackoffBetweenRetries_returns_30_seconds() {
    var event = new UuidCreated("test-uuid");
    assertEquals(Duration.ofSeconds(30), event.maxConsumerBackoffBetweenRetries());
  }

  @Test
  void noArgsConstructor_creates_empty() {
    var event = new UuidCreated();
    assertNull(event.getUuid());
  }
}
