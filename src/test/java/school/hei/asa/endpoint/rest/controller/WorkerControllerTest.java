package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import school.hei.asa.endpoint.rest.controller.mapper.ThContractMapper;
import school.hei.asa.endpoint.rest.controller.mapper.ThWorkerMapper;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.endpoint.rest.model.th.ThWorker;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.ThContractService;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.WorkerRepository;

class WorkerControllerTest {

  private final WorkerRepository workerRepository = mock(WorkerRepository.class);
  private final ContractRepository contractRepository = mock(ContractRepository.class);
  private final WorkerFromAuthentication workerFromAuthentication =
      mock(WorkerFromAuthentication.class);
  private final WorkerToModelAdder workerToModelAdder = mock(WorkerToModelAdder.class);
  private final ThWorkerMapper thWorkerMapper = mock(ThWorkerMapper.class);
  private final ThContractMapper thContractMapper = mock(ThContractMapper.class);
  private final ThContractService thContractService = mock(ThContractService.class);
  private final WorkerController controller =
      new WorkerController(
          workerRepository,
          contractRepository,
          workerFromAuthentication,
          workerToModelAdder,
          thWorkerMapper,
          thContractMapper,
          thContractService);

  @Test
  void getWorkers_returns_all_workers() {
    var workers =
        List.of(
            new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT"),
            new Worker("W-002", "Jane", "jane@test.com", "Jane", "Addr", "City", "NIF", "STAT"));
    when(workerRepository.findAll()).thenReturn(workers);

    var result = controller.getWorkers();

    assertEquals(workers, result);
  }

  @Test
  void getWorker_returns_worker_view() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var authentication = mock(Authentication.class);
    var model = new ConcurrentModel();
    var contracts = List.<Contract>of();
    var thWorker = new ThWorker("W-001", "John", "john@test.com", "Prestataire", null, null, null);

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(workerToModelAdder.apply(any(WorkerModelAdderParam.class), eq(model))).thenReturn(worker);
    when(contractRepository.findAllByWorker(any(Worker.class))).thenReturn(contracts);
    when(thWorkerMapper.toThWorker(worker, contracts)).thenReturn(thWorker);

    var result = controller.getWorker(model, authentication, null);

    assertEquals("worker", result);
    assertEquals(thWorker, model.getAttribute("worker"));
  }

  @Test
  void getContracts_returns_contracts_view() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var authentication = mock(Authentication.class);
    var model = new ConcurrentModel();
    var contracts = List.<Contract>of();
    var thContracts =
        List.of(
            new ThContract(
                "L1",
                "01 Jan 2025",
                "-",
                "Prestataire",
                null,
                "Company",
                "Dev",
                "365",
                "bucket-key",
                "200"));

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(workerToModelAdder.apply(any(WorkerModelAdderParam.class), eq(model))).thenReturn(worker);
    when(contractRepository.findAllByWorker(any(Worker.class))).thenReturn(contracts);
    when(thContractMapper.toTh(contracts)).thenReturn(thContracts);

    var result = controller.getContracts(model, authentication, null);

    assertEquals("contracts", result);
    assertEquals(worker, model.getAttribute("worker"));
    assertEquals("W-001", model.getAttribute("workerCode"));
    assertEquals(thContracts, model.getAttribute("contracts"));
    assertTrue((Boolean) model.getAttribute("firstContractEnded"));
  }
}
