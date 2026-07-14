package school.hei.asa.endpoint.event.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class ContractAlertRequestedTest {

  @Test
  void builder_sets_fields() {
    var event =
        ContractAlertRequested.builder()
            .workerName("John")
            .workerEmail("john@test.com")
            .remainingDays(3.0)
            .build();

    assertEquals("John", event.getWorkerName());
    assertEquals("john@test.com", event.getWorkerEmail());
    assertEquals(3.0, event.getRemainingDays());
  }

  @Test
  void maxConsumerDuration_returns_45_seconds() {
    var event = new ContractAlertRequested();
    assertEquals(Duration.ofSeconds(45), event.maxConsumerDuration());
  }

  @Test
  void maxConsumerBackoffBetweenRetries_returns_30_seconds() {
    var event = new ContractAlertRequested();
    assertEquals(Duration.ofSeconds(30), event.maxConsumerBackoffBetweenRetries());
  }

  @Test
  void toBuilder_creates_independent_copy() {
    var original =
        ContractAlertRequested.builder()
            .workerName("John")
            .workerEmail("john@test.com")
            .remainingDays(3.0)
            .build();

    var copy = original.toBuilder().workerName("Jane").build();

    assertEquals("John", original.getWorkerName());
    assertEquals("Jane", copy.getWorkerName());
    assertEquals("john@test.com", copy.getWorkerEmail());
  }

  @Test
  void noArgsConstructor_default_values() {
    var event = new ContractAlertRequested();
    assertNull(event.getWorkerName());
    assertNull(event.getWorkerEmail());
    assertEquals(0.0, event.getRemainingDays());
  }

  @Test
  void eventHandlerInitMaxDuration_returns_90_seconds() {
    var event = new ContractAlertRequested();
    assertEquals(Duration.ofSeconds(90), event.eventHandlerInitMaxDuration());
  }

  @Test
  void getEventSource_returns_event1() {
    var event = new ContractAlertRequested();
    assertEquals("school.hei.asa.event1", event.getEventSource());
  }

  @Test
  void attemptNb_can_be_set_and_get() {
    var event = new ContractAlertRequested();
    event.setAttemptNb(5);
    assertEquals(5, event.getAttemptNb());
  }
}
