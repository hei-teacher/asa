package school.hei.asa.endpoint.event.consumer.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.UuidCreated;

class ConsumableEventTest {

  @Test
  void ack_runs_acknowledger() {
    var payload = new UuidCreated("test-uuid");
    var typedEvent = new TypedEvent("school.hei.asa.endpoint.event.model.UuidCreated", payload);
    var acknowledged = new boolean[] {false};
    var consumable = new ConsumableEvent(typedEvent, () -> acknowledged[0] = true, () -> {});

    consumable.ack();

    assertTrue(acknowledged[0]);
  }

  @Test
  void newRandomVisibilityTimeout_runs_visibility_changer() {
    var payload = new UuidCreated("test-uuid");
    var typedEvent = new TypedEvent("school.hei.asa.endpoint.event.model.UuidCreated", payload);
    var visibilityChanged = new boolean[] {false};
    var consumable = new ConsumableEvent(typedEvent, () -> {}, () -> visibilityChanged[0] = true);

    consumable.newRandomVisibilityTimeout();

    assertTrue(visibilityChanged[0]);
  }

  @Test
  void getEvent_returns_typed_event() {
    var payload = new UuidCreated("test-uuid");
    var typedEvent = new TypedEvent("school.hei.asa.endpoint.event.model.UuidCreated", payload);
    var consumable = new ConsumableEvent(typedEvent, () -> {}, () -> {});

    assertEquals(typedEvent, consumable.getEvent());
  }
}
