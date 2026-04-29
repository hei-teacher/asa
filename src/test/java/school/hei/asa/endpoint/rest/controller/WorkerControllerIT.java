package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static school.hei.asa.model.contract.ContractType.studentContractor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.model.th.ThWorker;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.repository.ContractRepository;

class WorkerControllerIT extends FacadeIT {

  @Autowired WorkerController workerController;

  @MockBean WorkerFromAuthentication workerFromAuthentication;
  @MockBean WorkerToModelAdder workerToModelAdder;
  @MockBean ContractRepository contractRepository;

  Authentication authentication;
  Worker authenticatedWorker;
  Model model;

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    authenticatedWorker =
        new Worker(
            "worker-code",
            "Test Worker",
            "worker@example.com",
            "Full Worker Name",
            "address",
            "random city",
            "nif",
            "stat");
    model = mock(Model.class);
    var level = new ContractLevel("levelCode", studentContractor, null, 55_556d);
    var mockContract =
        new Contract(
            authenticatedWorker, "DevSecOps", level, Instant.now(), null, null, null, null);

    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    when(workerToModelAdder.apply(anyString(), any())).thenReturn(authenticatedWorker);
    when(contractRepository.findAllByWorker(any())).thenReturn(List.of(mockContract));
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
  void can_get_contracts_without_worker_code() {
    String viewName = workerController.getContracts(model, authentication, null);

    verify(model).addAttribute(eq("worker"), any(Worker.class));
    verify(model).addAttribute(eq("workerCode"), eq("worker-code"));
    verify(model).addAttribute(eq("contracts"), anyList());
    assertEquals("contracts", viewName);
  }

  @Test
  void can_get_contracts_with_worker_code() {
    String viewName = workerController.getContracts(model, authentication, "worker-code");

    verify(model).addAttribute(eq("worker"), any(Worker.class));
    verify(model).addAttribute(eq("workerCode"), eq("worker-code"));
    verify(model).addAttribute(eq("contracts"), anyList());
    assertEquals("contracts", viewName);
  }
}
