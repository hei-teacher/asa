package school.hei.asa.endpoint.rest.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.WorkerRepository;

class WorkerFromAuthenticationTest {

  private final WorkerRepository workerRepository = mock(WorkerRepository.class);
  private final WorkerFromAuthentication workerFromAuthentication =
      new WorkerFromAuthentication(workerRepository);

  @Test
  void apply_returns_worker_when_email_found() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var authentication = mock(Authentication.class);
    var oidcUser = mock(DefaultOidcUser.class);

    when(authentication.getPrincipal()).thenReturn(oidcUser);
    when(oidcUser.getEmail()).thenReturn("john@test.com");
    when(workerRepository.findByEmail("john@test.com")).thenReturn(Optional.of(worker));

    var result = workerFromAuthentication.apply(authentication);

    assertTrue(result.isPresent());
    assertEquals("W-001", result.get().code());
  }

  @Test
  void apply_returns_empty_when_email_not_found() {
    var authentication = mock(Authentication.class);
    var oidcUser = mock(DefaultOidcUser.class);

    when(authentication.getPrincipal()).thenReturn(oidcUser);
    when(oidcUser.getEmail()).thenReturn("unknown@test.com");
    when(workerRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

    var result = workerFromAuthentication.apply(authentication);

    assertTrue(result.isEmpty());
  }
}
