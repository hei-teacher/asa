package school.hei.asa.endpoint.event.consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import school.hei.asa.endpoint.event.consumer.model.TypedEvent;
import school.hei.asa.endpoint.event.model.UuidCreated;

class EventServiceInvokerTest {

  @Test
  void accept_with_matching_event_invokes_service() {
    var applicationContext = mock(ApplicationContext.class);
    var invoker = new EventServiceInvoker(applicationContext);

    var payload = new UuidCreated("test-uuid");
    var typedEvent = new TypedEvent("school.hei.asa.endpoint.event.model.UuidCreated", payload);

    assertThrows(Exception.class, () -> invoker.accept(typedEvent));
  }

  @Test
  void accept_with_unknown_type_throws() {
    var applicationContext = mock(ApplicationContext.class);
    var invoker = new EventServiceInvoker(applicationContext);

    var payload = new UuidCreated("test-uuid");
    var typedEvent = new TypedEvent("unknown.Type", payload);

    assertThrows(RuntimeException.class, () -> invoker.accept(typedEvent));
  }
}
