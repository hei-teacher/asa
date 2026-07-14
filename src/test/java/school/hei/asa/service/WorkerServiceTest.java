package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.WorkerRepository;

class WorkerServiceTest {

  private final WorkerRepository workerRepository = mock(WorkerRepository.class);
  private final SensitiveWorkerFilter sensitiveWorkerFilter = mock(SensitiveWorkerFilter.class);
  private final WorkerService workerService =
      new WorkerService(workerRepository, sensitiveWorkerFilter);

  @Test
  void findWorkerByCode() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(workerRepository.findByCode("W-001")).thenReturn(worker);

    var result = workerService.findWorkerByCode("W-001");

    assertEquals("W-001", result.code());
  }

  @Test
  void findWorkerByCode_not_found() {
    when(workerRepository.findByCode("UNKNOWN")).thenReturn(null);

    assertNull(workerService.findWorkerByCode("UNKNOWN"));
  }

  @Test
  void getAllWorkers_sorted_by_name() {
    var w1 = new Worker("W-002", "Bella", "bella@test.com", "Bella", "Addr", "City", "NIF", "STAT");
    var w2 = new Worker("W-001", "Alice", "alice@test.com", "Alice", "Addr", "City", "NIF", "STAT");
    when(workerRepository.findAll()).thenReturn(List.of(w1, w2));

    var result = workerService.getAllWorkers();

    assertEquals("Alice", result.get(0).name());
    assertEquals("Bella", result.get(1).name());
  }

  @Test
  void getAllWorkers_empty() {
    when(workerRepository.findAll()).thenReturn(List.of());

    assertTrue(workerService.getAllWorkers().isEmpty());
  }

  @Test
  void getWorkersFromModel_with_year_attribute() {
    var w1 = new Worker("W-001", "Alice", "alice@test.com", "Alice", "Addr", "City", "NIF", "STAT");
    var model = mock(Model.class);
    when(model.getAttribute("startDate")).thenReturn("2025-01-01");
    when(model.getAttribute("endDate")).thenReturn("2025-12-31");
    when(model.getAttribute("year")).thenReturn(2025);
    when(workerRepository.findByYearBetween(2025, 2026)).thenReturn(List.of(w1));

    var result = workerService.getWorkersFrom(model);

    assertEquals(1, result.size());
    assertEquals("Alice", result.getFirst().name());
  }

  @Test
  void getWorkersFromModel_without_year_falls_back_to_all() {
    var model = mock(Model.class);
    when(model.getAttribute("startDate")).thenReturn(null);
    when(model.getAttribute("endDate")).thenReturn(null);
    when(workerRepository.findAll()).thenReturn(List.of());

    var result = workerService.getWorkersFrom(model);

    assertEquals(0, result.size());
  }

  @Test
  void getWorkersFromModel_with_range_years() {
    var w1 = new Worker("W-001", "Alice", "alice@test.com", "Alice", "Addr", "City", "NIF", "STAT");
    var model = mock(Model.class);
    when(model.getAttribute("startDate")).thenReturn("2023-01-01");
    when(model.getAttribute("endDate")).thenReturn("2025-12-31");
    when(model.getAttribute("year")).thenReturn(null);
    when(workerRepository.findByYearBetween(2023, 2025)).thenReturn(List.of(w1));

    var result = workerService.getWorkersFrom(model);

    assertEquals(1, result.size());
    assertEquals("Alice", result.getFirst().name());
  }
}
