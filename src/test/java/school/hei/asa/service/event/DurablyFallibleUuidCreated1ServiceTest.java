package school.hei.asa.service.event;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.DurablyFallibleUuidCreated1;
import school.hei.asa.endpoint.event.model.UuidCreated;

class DurablyFallibleUuidCreated1ServiceTest {

  @Test
  void accept_without_failure_delegates_to_uuidCreatedService() {
    var uuidCreatedService = mock(UuidCreatedService.class);
    var service = new DurablyFallibleUuidCreated1Service(uuidCreatedService);

    var uuidCreated = new UuidCreated("test-uuid");
    var event =
        DurablyFallibleUuidCreated1.builder()
            .uuidCreated(uuidCreated)
            .waitDurationBeforeConsumingInSeconds(0)
            .failureRate(0.0)
            .build();

    service.accept(event);

    verify(uuidCreatedService).accept(uuidCreated);
  }

  @Test
  void accept_with_failure_throws() {
    var uuidCreatedService = mock(UuidCreatedService.class);
    var service = new DurablyFallibleUuidCreated1Service(uuidCreatedService);

    var uuidCreated = new UuidCreated("test-uuid");
    var event =
        DurablyFallibleUuidCreated1.builder()
            .uuidCreated(uuidCreated)
            .waitDurationBeforeConsumingInSeconds(0)
            .failureRate(1.0)
            .build();

    assertThrows(RuntimeException.class, () -> service.accept(event));
    verify(uuidCreatedService, never()).accept(any());
  }
}
