package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.model.*;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.model.contract.ContractType;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

class ContractServiceWorkedDaysTest {

  private final WorkerRepository workerRepository = mock(WorkerRepository.class);
  private final ContractRepository contractRepository = mock(ContractRepository.class);
  private final DailyExecutionRepository dailyExecutionRepository =
      mock(DailyExecutionRepository.class);
  private final MissionService missionService = mock(MissionService.class);
  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);
  private final ContractService contractService =
      new ContractService(
          workerRepository,
          contractRepository,
          dailyExecutionRepository,
          missionService,
          careProductCodeSupplier);

  private final Worker worker =
      new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

  @Test
  void getActualWorkedDays_fullWork() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    var product = new Product("WORK", "Work", "D");
    var mission = new Mission("M01", "M", "D", 10, product);
    var date = LocalDate.of(2025, 6, 1);
    var me = new MissionExecution(mission, worker, date, 1.0, "c", Instant.now());
    var de = new DailyExecution(worker, date, List.of(me));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));

    var result = contractService.getActualWorkedDaysByDateByWorker(date, "W-001", date);

    // Locale-dependent formatting: French locale gives "1,0"
    var expected = String.format("%.1f", 1.0);
    assertEquals(expected, result);
  }

  @Test
  void getActualWorkedDays_fullCare() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    var product = new Product("CARE", "Care", "D");
    var mission = new Mission("M01", "M", "D", 10, product);
    var date = LocalDate.of(2025, 6, 1);
    var me = new MissionExecution(mission, worker, date, 1.0, "c", Instant.now());
    var de = new DailyExecution(worker, date, List.of(me));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));

    var result = contractService.getActualWorkedDaysByDateByWorker(date, "W-001", date);

    var expected = String.format("%.1f", 0.0);
    assertEquals(expected, result);
  }

  @Test
  void getActualWorkedDays_mixed() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    var careProduct = new Product("CARE", "Care", "D");
    var workProduct = new Product("WORK", "Work", "D");
    var careMission = new Mission("C01", "Care", "D", 10, careProduct);
    var workMission = new Mission("W01", "Work", "D", 10, workProduct);
    var date = LocalDate.of(2025, 6, 1);
    var me1 = new MissionExecution(careMission, worker, date, 0.5, "c", Instant.now());
    var me2 = new MissionExecution(workMission, worker, date, 0.5, "w", Instant.now());
    var de = new DailyExecution(worker, date, List.of(me1, me2));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    var result = contractService.getActualWorkedDaysByDateByWorker(date, "W-001", date);

    // 0.5 (work) + 0.5 (care but isUnpaidCare=false) = 1.0
    var expected = String.format("%.1f", 1.0);
    assertEquals(expected, result);
  }

  @Test
  void hasRemainingDays_partner_with_days_remaining() {
    var entrance = Instant.now().minus(Duration.ofDays(1));
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("L1", ContractType.partnerContractor, null, 50_000.0),
            entrance,
            null,
            Duration.ofDays(100),
            "Company",
            null);
    when(contractRepository.findAllByWorker(worker)).thenReturn(List.of(contract));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of());

    assertTrue(contractService.hasRemainingDays(worker));
  }

  @Test
  void hasRemainingDays_partner_with_no_remaining_days() {
    var entrance = Instant.now().minus(Duration.ofDays(100));
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("L1", ContractType.partnerContractor, null, 50_000.0),
            entrance,
            null,
            Duration.ofDays(0),
            "Company",
            null);
    when(contractRepository.findAllByWorker(worker)).thenReturn(List.of(contract));
    var product = new Product("WORK", "Work", "D");
    var mission = new Mission("M01", "M", "D", 10, product);
    var date = LocalDate.now();
    var me = new MissionExecution(mission, worker, date, 1.0, "c", Instant.now());
    var de = new DailyExecution(worker, date, List.of(me));
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(de));

    assertFalse(contractService.hasRemainingDays(worker));
  }
}
