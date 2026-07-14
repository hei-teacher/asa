package school.hei.asa.endpoint.event.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class PojaEventTest {

  @Test
  void eventHandlerInitMaxDuration_returns_90_seconds() {
    var event = new TestPojaEvent();
    assertEquals(Duration.ofSeconds(90), event.eventHandlerInitMaxDuration());
  }

  @Test
  void randomVisibilityTimeout_returns_positive_duration() {
    var event = new TestPojaEvent();
    var timeout = event.randomVisibilityTimeout();
    assertTrue(timeout.toSeconds() > 0);
  }

  @Test
  void getEventSource_returns_event1_for_default_stack() {
    var event = new TestPojaEvent();
    assertEquals("school.hei.asa.event1", event.getEventSource());
  }

  @Test
  void attemptNb_can_be_set_and_get() {
    var event = new TestPojaEvent();
    event.setAttemptNb(3);
    assertEquals(3, event.getAttemptNb());
  }

  private static class TestPojaEvent extends PojaEvent {
    @Override
    public Duration maxConsumerDuration() {
      return Duration.ofSeconds(10);
    }

    @Override
    public Duration maxConsumerBackoffBetweenRetries() {
      return Duration.ofSeconds(30);
    }
  }
}
