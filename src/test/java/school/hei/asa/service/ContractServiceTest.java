package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static school.hei.asa.model.contract.ContractType.partnerContractor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

class ContractServiceTest {

  private ContractRepository contractRepository;
  private DailyExecutionRepository dailyExecutionRepository;
  private MissionService missionService;
  private ContractService contractService;
  private Worker worker;

  @BeforeEach
  void setUp() {
    var workerRepository = mock(WorkerRepository.class);
    contractRepository = mock(ContractRepository.class);
    dailyExecutionRepository = mock(DailyExecutionRepository.class);
    missionService = mock(MissionService.class);

    contractService =
        new ContractService(
            workerRepository, contractRepository, dailyExecutionRepository, missionService);

    worker = new Worker("W-P-2024-01", "Lita Andria", "", "", "", "", "", "", null, null);
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
  void open_ended_contract_counts_future_executions_window() {
    var contract = newContract(worker, 11, Instant.parse("2026-10-28T08:00:00Z"), null);
    when(contractRepository.findActiveContractByWorker(worker)).thenReturn(Optional.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(
            "W-P-2024-01", LocalDate.of(2026, 10, 28), LocalDate.of(9999, 12, 31)))
        .thenReturn(List.of());

    var actual = contractService.getRemainingDaysOnActiveContractOrZero(worker);

    assertEquals(11d, actual);
  }

  @Test
  void find_active_contract_delegates_to_repository() {
    var contract = newContract(worker, 80, Instant.parse("2025-01-01T08:00:00Z"), null);
    when(contractRepository.findActiveContractByWorker(worker)).thenReturn(Optional.of(contract));

    var actual = contractService.findActiveContractByWorker(worker);

    assertTrue(actual.isPresent());
    assertEquals(contract, actual.get());
  }

  @Test
  void paid_care_day_consumes_contract_days() {
    var actual = remainingDaysAfter(dailyExecution(execution("CA-TB", 1.0d)), "CA-TB", false);

    assertEquals(79d, actual);
  }

  @Test
  void unpaid_care_day_does_not_consume_contract_days() {
    var actual = remainingDaysAfter(dailyExecution(execution("CA-ABNP", 1.0d)), "CA-ABNP", true);

    assertEquals(80d, actual);
  }

  @Test
  void mixed_work_and_unpaid_care_day_consumes_only_work() {
    var actual =
        remainingDaysAfter(
            dailyExecution(execution("M1", 0.5d), execution("CA-ABNP", 0.5d)), "CA-ABNP", true);

    assertEquals(79.5d, actual);
  }

  private double remainingDaysAfter(
      DailyExecution dailyExecution, String careMissionCode, boolean isUnpaidCare) {
    var contract = newContract(worker, 80, Instant.parse("2025-01-01T08:00:00Z"), null);
    when(contractRepository.findActiveContractByWorker(worker)).thenReturn(Optional.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(
            anyString(), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of(dailyExecution));
    when(missionService.isUnpaidCare(any()))
        .thenAnswer(
            invocation -> {
              MissionExecution me = invocation.getArgument(0);
              return isUnpaidCare && me.mission().code().equals(careMissionCode);
            });
    return contractService.getRemainingDaysOnActiveContractOrZero(worker);
  }

  private DailyExecution dailyExecution(MissionExecution... executions) {
    return new DailyExecution(worker, LocalDate.of(2025, 1, 2), List.of(executions));
  }

  private MissionExecution execution(String missionCode, double dayPercentage) {
    var product = new Product(missionCode.startsWith("CA") ? "CA" : "WORK", "p", "desc");
    var mission = new Mission(missionCode, missionCode, "desc", 5, product);
    return new MissionExecution(
        mission, worker, LocalDate.of(2025, 1, 2), dayPercentage, "comment", null);
  }

  private Contract newContract(
      Worker worker, int durationInDays, Instant entranceInstant, Instant endInstant) {
    return new Contract(
        worker,
        "job_title",
        new ContractLevel("L5P-2026", partnerContractor, 0d, 50000d, null),
        entranceInstant,
        endInstant,
        Duration.ofDays(durationInDays),
        "company",
        "contract_bucket_key");
  }
}
