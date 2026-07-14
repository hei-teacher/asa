package school.hei.asa.endpoint.event.consumer;

import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.concurrency.Workers;
import school.hei.asa.endpoint.event.consumer.model.ConsumableEvent;
import school.hei.asa.endpoint.event.consumer.model.TypedEvent;
import school.hei.asa.endpoint.event.model.UuidCreated;

class EventConsumerTest {

  @Test
  void accept_invokes_workers() {
    var workers = mock(Workers.class);
    var eventServiceInvoker = mock(EventServiceInvoker.class);
    var consumer = new EventConsumer(workers, eventServiceInvoker);

    var payload = new UuidCreated("test-uuid");
    var typedEvent = new TypedEvent("school.hei.asa.endpoint.event.model.UuidCreated", payload);
    var consumableEvent = new ConsumableEvent(typedEvent, () -> {}, () -> {});

    consumer.accept(List.of(consumableEvent));

    verify(workers).invokeAll(anyList());
  }

  @Test
  void accept_with_empty_list_does_nothing() {
    var workers = mock(Workers.class);
    var eventServiceInvoker = mock(EventServiceInvoker.class);
    var consumer = new EventConsumer(workers, eventServiceInvoker);

    consumer.accept(List.of());

    verify(workers).invokeAll(List.of());
  }
}
