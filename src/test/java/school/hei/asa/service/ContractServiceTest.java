package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

class ContractServiceTest {

  private ContractRepository contractRepository;
  private DailyExecutionRepository dailyExecutionRepository;
  private CareProductCodeSupplier careProductCodeSupplier;
  private ContractService contractService;
  private Worker worker;

  @BeforeEach
  void setUp() {
    var workerRepository = mock(WorkerRepository.class);
    contractRepository = mock(ContractRepository.class);
    dailyExecutionRepository = mock(DailyExecutionRepository.class);
    var missionService = mock(MissionService.class);
    careProductCodeSupplier = mock(CareProductCodeSupplier.class);

    contractService =
        new ContractService(
            workerRepository,
            contractRepository,
            dailyExecutionRepository,
            missionService,
            careProductCodeSupplier);

    worker = new Worker("W-P-2024-01", "Lita Andria", "", "", "", "", "", "");
  }

  @Test
  void no_active_contract_returns_zero_remaining_days() {
    when(contractRepository.findActiveContractByWorker(worker)).thenReturn(Optional.empty());

    var actual = contractService.getRemainingDaysOnActiveContractOrZero(worker);

    assertEquals(0d, actual);
  }

  @Test
  void active_contract_without_executions_returns_full_duration() {
    var contract = newContract(worker, 80, Instant.parse("2025-01-01T08:00:00Z"), null);
    when(contractRepository.findActiveContractByWorker(worker)).thenReturn(Optional.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(
            anyString(), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of());

    var actual = contractService.getRemainingDaysOnActiveContractOrZero(worker);

    assertEquals(80d, actual);
  }

  @Test
  void active_contract_with_executions_subtracts_worked_days() {
    var contract = newContract(worker, 80, Instant.parse("2025-01-01T08:00:00Z"), null);
    when(contractRepository.findActiveContractByWorker(worker)).thenReturn(Optional.of(contract));

    var product = new Product("WORK-PRODUCT", "Work", "desc");
    var mission = new Mission("M1", "Mission 1", "desc", 5, product);
    var execution =
        new MissionExecution(mission, worker, LocalDate.of(2025, 1, 2), 1.0d, "comment", null);
    var dailyExecution = new DailyExecution(worker, LocalDate.of(2025, 1, 2), List.of(execution));

    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(
            anyString(), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of(dailyExecution));
    when(careProductCodeSupplier.get()).thenReturn("CARE-PRODUCT");

    var actual = contractService.getRemainingDaysOnActiveContractOrZero(worker);

    assertEquals(79d, actual);
  }

  @Test
  void ended_contract_uses_end_instant_as_upper_bound() {
    var contract =
        newContract(
            worker,
            80,
            Instant.parse("2024-01-01T08:00:00Z"),
            Instant.parse("2024-06-01T08:00:00Z"));
    when(contractRepository.findActiveContractByWorker(worker)).thenReturn(Optional.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(
            "W-P-2024-01", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 1)))
        .thenReturn(List.of());

    var actual = contractService.getRemainingDaysOnActiveContractOrZero(worker);

    assertEquals(80d, actual);
  }

  @Test
  void find_active_contract_delegates_to_repository() {
    var contract = newContract(worker, 80, Instant.parse("2025-01-01T08:00:00Z"), null);
    when(contractRepository.findActiveContractByWorker(worker)).thenReturn(Optional.of(contract));

    var actual = contractService.findActiveContractByWorker(worker);

    assertTrue(actual.isPresent());
    assertEquals(contract, actual.get());
  }

  private Contract newContract(
      Worker worker, int durationInDays, Instant entranceInstant, Instant endInstant) {
    return new Contract(
        worker,
        "job_title",
        null,
        entranceInstant,
        endInstant,
        Duration.ofDays(durationInDays),
        "company",
        "contract_bucket_key");
  }
}
