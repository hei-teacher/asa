package school.hei.asa.endpoint;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.CalendarController;
import school.hei.asa.endpoint.rest.controller.WorkerToModelAdder;
import school.hei.asa.endpoint.rest.model.th.ThYear;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.Worker;

class CalendarControllerIT extends FacadeIT {

  @Autowired CalendarController calendarController;

  @MockBean WorkerFromAuthentication workerFromAuthentication;
  @MockBean WorkerToModelAdder workerToModelAdder;

  Authentication authentication;
  Worker authenticatedWorker;
  Model model;

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    authenticatedWorker = new PartnerContractor("worker-code", "Test Worker", "worker@example.com");
    model = mock(Model.class);

    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    when(workerToModelAdder.apply(anyString(), any())).thenReturn(authenticatedWorker);
  }

  @Test
  void can_get_calendar_without_worker_code() {
    String viewName = calendarController.getCalendar(model, authentication, null);

    verify(model).addAttribute(eq("year"), eq(now().getYear()));
    verify(model).addAttribute(eq("thYear"), any(ThYear.class));
    assertEquals("calendar", viewName);
  }

  @Test
  void can_get_calendar_with_worker_code() {
    String viewName = calendarController.getCalendar(model, authentication, "worker-code");

    verify(model).addAttribute(eq("year"), eq(now().getYear()));
    verify(model).addAttribute(eq("thYear"), any(ThYear.class));
    assertEquals("calendar", viewName);
  }
}
