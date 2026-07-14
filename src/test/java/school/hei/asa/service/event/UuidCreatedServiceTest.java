package school.hei.asa.service.event;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.UuidCreated;
import school.hei.asa.repository.DummyUuidRepository;

class UuidCreatedServiceTest {

  @Test
  void accept_saves_dummy_uuid() {
    var dummyUuidRepository = mock(DummyUuidRepository.class);
    var service = new UuidCreatedService(dummyUuidRepository);

    var event = new UuidCreated("test-uuid");
    service.accept(event);

    verify(dummyUuidRepository).save(argThat(dummy -> dummy.getId().equals("test-uuid")));
  }
}
