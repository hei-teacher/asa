package school.hei.asa.endpoint.event.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class DurablyFallibleUuidCreated1Test {

  @Test
  void builder_creates_event() {
    var uuidCreated = new UuidCreated("test-uuid");
    var event =
        DurablyFallibleUuidCreated1.builder()
            .uuidCreated(uuidCreated)
            .waitDurationBeforeConsumingInSeconds(5)
            .failureRate(0.0)
            .build();

    assertEquals(uuidCreated, event.getUuidCreated());
    assertEquals(5, event.getWaitDurationBeforeConsumingInSeconds());
    assertEquals(0.0, event.getFailureRate());
  }

  @Test
  void shouldFail_returns_false_when_failureRate_is_zero() {
    var uuidCreated = new UuidCreated("test-uuid");
    var event =
        DurablyFallibleUuidCreated1.builder()
            .uuidCreated(uuidCreated)
            .waitDurationBeforeConsumingInSeconds(5)
            .failureRate(0.0)
            .build();

    assertFalse(event.shouldFail());
  }

  @Test
  void shouldFail_returns_true_when_failureRate_is_one() {
    var uuidCreated = new UuidCreated("test-uuid");
    var event =
        DurablyFallibleUuidCreated1.builder()
            .uuidCreated(uuidCreated)
            .waitDurationBeforeConsumingInSeconds(5)
            .failureRate(1.0)
            .build();

    assertTrue(event.shouldFail());
  }

  @Test
  void maxConsumerDuration_includes_wait_and_uuid_duration() {
    var uuidCreated = new UuidCreated("test-uuid");
    var event =
        DurablyFallibleUuidCreated1.builder()
            .uuidCreated(uuidCreated)
            .waitDurationBeforeConsumingInSeconds(5)
            .failureRate(0.0)
            .build();

    assertEquals(Duration.ofSeconds(15), event.maxConsumerDuration());
  }

  @Test
  void maxConsumerBackoffBetweenRetries_delegates_to_uuidCreated() {
    var uuidCreated = new UuidCreated("test-uuid");
    var event =
        DurablyFallibleUuidCreated1.builder()
            .uuidCreated(uuidCreated)
            .waitDurationBeforeConsumingInSeconds(5)
            .failureRate(0.0)
            .build();

    assertEquals(Duration.ofSeconds(30), event.maxConsumerBackoffBetweenRetries());
  }

  @Test
  void noArgsConstructor_creates_empty() {
    var event = new DurablyFallibleUuidCreated1();
    assertNull(event.getUuidCreated());
  }
}
