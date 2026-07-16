package school.hei.asa.service;

import static java.util.Locale.FRENCH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static school.hei.asa.model.contract.ContractType.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.ContractAlertRequested;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

class ContractServiceTest {

  private final WorkerRepository workerRepository = mock(WorkerRepository.class);
  private final ContractRepository contractRepository = mock(ContractRepository.class);
  private final DailyExecutionRepository dailyExecutionRepository =
      mock(DailyExecutionRepository.class);
  private final MissionExecutionRepository missionExecutionRepository =
      mock(MissionExecutionRepository.class);
  private final MissionService missionService = mock(MissionService.class);
  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);

  @SuppressWarnings("unchecked")
  private final EventProducer<ContractAlertRequested> eventProducer = mock(EventProducer.class);

  private final int alertThreshold = 10;
  private final ContractService contractService =
      new ContractService(
          workerRepository,
          contractRepository,
          dailyExecutionRepository,
          missionExecutionRepository,
          missionService,
          careProductCodeSupplier,
          eventProducer,
          alertThreshold);

  @Test
  void totalWorkDaysPerWorker() {
    var w = new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contracts =
        List.of(
            new Contract(
                w,
                "Job",
                new ContractLevel("L1", partnerContractor, null, 50_000.0),
                Instant.now(),
                null,
                Duration.ofDays(100),
                "Company",
                null));
    when(contractRepository.findAll()).thenReturn(contracts);

    var result = contractService.totalWorkDaysPerWorker();

    assertEquals(1, result.size());
    assertTrue(result.containsKey(w));
  }

  @Test
  void totalWorkDaysForOneWorker() {
    var w = new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(workerRepository.findByCode("W-001")).thenReturn(w);
    when(contractRepository.findAllByWorker(w)).thenReturn(List.of());

    var result = contractService.totalWorkDaysForOneWorker("W-001");

    assertEquals(1, result.size());
    assertTrue(result.containsKey(w));
  }

  @Test
  void getActualWorkedDaysByDateByWorker_without_executions_returns_dash() {
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of());

    var result =
        contractService.getActualWorkedDaysByDateByWorker(
            LocalDate.now(), "W-001", LocalDate.now());

    assertEquals("-", result);
  }

  @Test
  void getActualWorkedDaysByDateByWorker_with_executions_returns_formatted() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var product = new Product("PCODE", "pname", "desc");
    var mission = new Mission("M001", "title", "desc", 10, product);
    var me = new MissionExecution(mission, worker, LocalDate.now(), 1.0, "comment", Instant.now());
    var dailyExecution = new DailyExecution(worker, LocalDate.now(), List.of(me));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(dailyExecution));
    when(careProductCodeSupplier.get()).thenReturn("CARE");

    var result =
        contractService.getActualWorkedDaysByDateByWorker(
            LocalDate.now(), "W-001", LocalDate.now());

    assertEquals(String.format(FRENCH, "%.1f", 1.0), result);
  }

  @Test
  void hasRemainingDays_with_no_active_contract_returns_false() {
    var w = new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(contractRepository.findAllByWorker(w)).thenReturn(List.of());

    assertFalse(contractService.hasRemainingDays(w));
  }

  @Test
  void hasRemainingDays_fte_returns_true() {
    var w = new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            w,
            "Job",
            new ContractLevel("FTE", fullTimeEmployee, 2_000_000.0, null),
            Instant.now(),
            null,
            Duration.ofDays(365),
            "Company",
            null);
    when(contractRepository.findAllByWorker(w)).thenReturn(List.of(contract));

    assertTrue(contractService.hasRemainingDays(w));
  }

  @Test
  void hasRemainingDays_active_contract_with_remaining_days_returns_true() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("L1", partnerContractor, null, 50_000.0),
            Instant.now().minus(Duration.ofDays(30)),
            null,
            Duration.ofDays(10),
            "Company",
            null);
    var product = new Product("WORK", "Work", "D");
    var careProduct = new Product("CARE", "Care", "D");
    var careMission = new Mission("CARE", "Care", "D", 10, careProduct);
    var workMission = new Mission("WORK", "Work", "D", 10, product);
    var date = LocalDate.now();
    var meCare = new MissionExecution(careMission, worker, date, 0.0, "c", Instant.now());
    var meWork = new MissionExecution(workMission, worker, date, 1.0, "w", Instant.now());
    var de = new DailyExecution(worker, date, List.of(meCare, meWork));
    when(contractRepository.findAllByWorker(worker)).thenReturn(List.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    assertTrue(contractService.hasRemainingDays(worker));
  }

  @Test
  void hasRemainingDays_active_contract_without_remaining_days_returns_false() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("L1", partnerContractor, null, 50_000.0),
            Instant.now().minus(Duration.ofDays(30)),
            null,
            Duration.ofDays(1),
            "Company",
            null);
    var product = new Product("WORK", "Work", "D");
    var careProduct = new Product("CARE", "Care", "D");
    var careMission = new Mission("CARE", "Care", "D", 10, careProduct);
    var workMission = new Mission("WORK", "Work", "D", 10, product);
    var date = LocalDate.now();
    var meCare = new MissionExecution(careMission, worker, date, 0.0, "c", Instant.now());
    var meWork = new MissionExecution(workMission, worker, date, 1.0, "w", Instant.now());
    var de = new DailyExecution(worker, date, List.of(meCare, meWork));
    when(contractRepository.findAllByWorker(worker)).thenReturn(List.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    assertFalse(contractService.hasRemainingDays(worker));
  }

  @Test
  void remainingDays_no_active_contract_returns_negative() {
    var w = new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(contractRepository.findAllByWorker(w)).thenReturn(List.of());

    assertEquals(-1, contractService.getRemainingDaysByWorker(w));
  }

  @Test
  void remainingDays_fte_returns_max_value() {
    var w = new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            w,
            "Job",
            new ContractLevel("FTE", fullTimeEmployee, 2_000_000.0, null),
            Instant.now(),
            null,
            Duration.ZERO,
            "Company",
            null);
    when(contractRepository.findAllByWorker(w)).thenReturn(List.of(contract));

    assertEquals(Long.MAX_VALUE, contractService.getRemainingDaysByWorker(w));
  }

  @Test
  void remainingDays_active_contract_returns_remaining() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("L1", partnerContractor, null, 50_000.0),
            Instant.now().minus(Duration.ofDays(30)),
            null,
            Duration.ofDays(10),
            "Company",
            null);
    var product = new Product("WORK", "Work", "D");
    var careProduct = new Product("CARE", "Care", "D");
    var careMission = new Mission("CARE", "Care", "D", 10, careProduct);
    var workMission = new Mission("WORK", "Work", "D", 10, product);
    var date = LocalDate.now();
    var meCare = new MissionExecution(careMission, worker, date, 0.0, "c", Instant.now());
    var meWork = new MissionExecution(workMission, worker, date, 1.0, "w", Instant.now());
    var de = new DailyExecution(worker, date, List.of(meCare, meWork));
    when(contractRepository.findAllByWorker(worker)).thenReturn(List.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    assertEquals(9L, contractService.getRemainingDaysByWorker(worker));
  }

  @Test
  void findActiveContracts() {
    when(contractRepository.findAllActiveContracts()).thenReturn(List.of());

    var result = contractService.findActiveContracts();

    assertNotNull(result);
  }

  @Test
  void getAllContractsByWorker() {
    var w = new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(contractRepository.findAllByWorker(w)).thenReturn(List.of());

    var result = contractService.getAllContractsByWorker(w);

    assertNotNull(result);
  }

  @Test
  void checkAndNotifyContractAlert_no_remaining_returns_empty() {
    var w = new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    when(contractRepository.findAllByWorker(w)).thenReturn(List.of());

    var result = contractService.checkAndNotifyContractAlert(w);

    assertTrue(result.isEmpty());
    verify(eventProducer, never()).accept(any());
  }

  @Test
  void checkAndNotifyContractAlert_above_threshold_returns_empty() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("L1", partnerContractor, null, 50_000.0),
            Instant.now().minus(Duration.ofDays(1)),
            null,
            Duration.ofDays(100),
            "Company",
            null);
    var product = new Product("WORK", "Work", "D");
    var careProduct = new Product("CARE", "Care", "D");
    var careMission = new Mission("CARE", "Care", "D", 10, careProduct);
    var workMission = new Mission("WORK", "Work", "D", 10, product);
    var date = LocalDate.now();
    var meCare = new MissionExecution(careMission, worker, date, 0.0, "c", Instant.now());
    var meWork = new MissionExecution(workMission, worker, date, 1.0, "w", Instant.now());
    var de = new DailyExecution(worker, date, List.of(meCare, meWork));
    when(contractRepository.findAllByWorker(worker)).thenReturn(List.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    var result = contractService.checkAndNotifyContractAlert(worker);

    assertTrue(result.isEmpty());
    verify(eventProducer, never()).accept(any());
  }

  @Test
  void checkAndNotifyContractAlert_below_threshold_sends_event() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("L1", partnerContractor, null, 50_000.0),
            Instant.now().minus(Duration.ofDays(30)),
            null,
            Duration.ofDays(10),
            "Company",
            null);
    var product = new Product("WORK", "Work", "D");
    var careProduct = new Product("CARE", "Care", "D");
    var careMission = new Mission("CARE", "Care", "D", 10, careProduct);
    var workMission = new Mission("WORK", "Work", "D", 10, product);
    var date = LocalDate.now();
    var meCare = new MissionExecution(careMission, worker, date, 0.0, "c", Instant.now());
    var meWork = new MissionExecution(workMission, worker, date, 5.0, "w", Instant.now());
    var de = new DailyExecution(worker, date, List.of(meCare, meWork));
    when(contractRepository.findAllByWorker(worker)).thenReturn(List.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    var result = contractService.checkAndNotifyContractAlert(worker);

    assertTrue(result.isPresent());
    assertEquals("Warning: only 5 days left on your contract.", result.get());
    verify(eventProducer).accept(any());
  }

  @Test
  void checkAndNotifyContractAlert_singular_day() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("L1", partnerContractor, null, 50_000.0),
            Instant.now().minus(Duration.ofDays(30)),
            null,
            Duration.ofDays(10),
            "Company",
            null);
    var product = new Product("WORK", "Work", "D");
    var careProduct = new Product("CARE", "Care", "D");
    var careMission = new Mission("CARE", "Care", "D", 10, careProduct);
    var workMission = new Mission("WORK", "Work", "D", 10, product);
    var date = LocalDate.now();
    var meCare = new MissionExecution(careMission, worker, date, 0.0, "c", Instant.now());
    var meWork = new MissionExecution(workMission, worker, date, 9.0, "w", Instant.now());
    var de = new DailyExecution(worker, date, List.of(meCare, meWork));
    when(contractRepository.findAllByWorker(worker)).thenReturn(List.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    var result = contractService.checkAndNotifyContractAlert(worker);

    assertTrue(result.isPresent());
    assertEquals("Warning: only 1 day left on your contract.", result.get());
    verify(eventProducer).accept(any());
  }

  @Test
  void checkAndNotifyContractAlert_eventProducer_failure_still_returns_message() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("L1", partnerContractor, null, 50_000.0),
            Instant.now().minus(Duration.ofDays(30)),
            null,
            Duration.ofDays(10),
            "Company",
            null);
    var product = new Product("WORK", "Work", "D");
    var careProduct = new Product("CARE", "Care", "D");
    var careMission = new Mission("CARE", "Care", "D", 10, careProduct);
    var workMission = new Mission("WORK", "Work", "D", 10, product);
    var date = LocalDate.now();
    var meCare = new MissionExecution(careMission, worker, date, 0.0, "c", Instant.now());
    var meWork = new MissionExecution(workMission, worker, date, 3.0, "w", Instant.now());
    var de = new DailyExecution(worker, date, List.of(meCare, meWork));
    when(contractRepository.findAllByWorker(worker)).thenReturn(List.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(missionService.isUnpaidCare(any())).thenReturn(false);
    doThrow(new RuntimeException("EventBridge down")).when(eventProducer).accept(any());

    var result = contractService.checkAndNotifyContractAlert(worker);

    assertTrue(result.isPresent());
    assertEquals("Warning: only 7 days left on your contract.", result.get());
  }
}
