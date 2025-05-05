package school.hei.asa.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import school.hei.asa.endpoint.rest.controller.WorkerController;
import school.hei.asa.endpoint.rest.controller.WorkerToModelAdder;
import school.hei.asa.endpoint.rest.model.th.ThWorker;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.Worker;

class WorkerControllerIT extends FacadeIT {

  @Autowired WorkerController workerController;

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
  void can_get_workers_list() {
    assertTrue(workerController.getWorkers().toString().contains("Lita"));
  }

  @Test
  void can_get_worker_without_worker_code() {
    String viewName = workerController.getWorker(model, authentication, null);

    verify(model).addAttribute(eq("worker"), any(ThWorker.class));
    assertEquals("worker", viewName);
  }

  @Test
  void can_get_worker_with_worker_code() {
    String viewName = workerController.getWorker(model, authentication, "worker-code");

    verify(model).addAttribute(eq("worker"), any(ThWorker.class));
    assertEquals("worker", viewName);
  }

  @Test
  void can_get_worker_level_history_without_worker_code() {
    String viewName = workerController.getWorkersLevelHistory(model, authentication, null);

    verify(model).addAttribute(eq("worker"), any(Worker.class));
    verify(model).addAttribute(eq("workerCode"), eq("worker-code"));
    verify(model).addAttribute(eq("workerLevelHistory"), anyList());
    assertEquals("worker-level-history", viewName);
  }

  @Test
  void can_get_worker_level_history_with_worker_code() {
    String viewName = workerController.getWorkersLevelHistory(model, authentication, "worker-code");

    verify(model).addAttribute(eq("worker"), any(Worker.class));
    verify(model).addAttribute(eq("workerCode"), eq("worker-code"));
    verify(model).addAttribute(eq("workerLevelHistory"), anyList());
    assertEquals("worker-level-history", viewName);
  }
}
