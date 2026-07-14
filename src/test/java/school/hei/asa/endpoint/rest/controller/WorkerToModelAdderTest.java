package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.model.Worker;
import school.hei.asa.service.SensitiveWorkerFilter;
import school.hei.asa.service.WorkerService;

class WorkerToModelAdderTest {

  @Test
  void apply_with_workerCode_finds_worker() {
    var workerService = mock(WorkerService.class);
    var sensitiveWorkerFilter = mock(SensitiveWorkerFilter.class);
    var adder = new WorkerToModelAdder(workerService, sensitiveWorkerFilter);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(workerService.findWorkerByCode("W-001")).thenReturn(worker);
    when(workerService.getWorkersFrom(any(Model.class))).thenReturn(List.of(worker));
    when(sensitiveWorkerFilter.filterSensitiveWorkers(anyList(), anyString()))
        .thenReturn(List.of(worker));

    Model model = new BindingAwareModelMap();
    var param = new WorkerModelAdderParam("W-001", "auth-user");
    var result = adder.apply(param, model);

    assertEquals(worker, result);
    assertEquals(worker, model.getAttribute("worker"));
    assertEquals("John", model.getAttribute("workerName"));
    assertEquals(List.of(worker), model.getAttribute("workers"));
  }

  @Test
  void apply_with_null_workerCode_uses_authenticated() {
    var workerService = mock(WorkerService.class);
    var sensitiveWorkerFilter = mock(SensitiveWorkerFilter.class);
    var adder = new WorkerToModelAdder(workerService, sensitiveWorkerFilter);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(workerService.findWorkerByCode("auth-user")).thenReturn(worker);
    when(workerService.getWorkersFrom(any(Model.class))).thenReturn(List.of(worker));
    when(sensitiveWorkerFilter.filterSensitiveWorkers(anyList(), anyString()))
        .thenReturn(List.of(worker));

    Model model = new BindingAwareModelMap();
    var param = new WorkerModelAdderParam(null, "auth-user");
    var result = adder.apply(param, model);

    assertEquals(worker, result);
  }
}
