package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

class FinancialPlanServiceConcreteTest {

  private final ContractRepository contractRepository = mock(ContractRepository.class);
  private final InvoiceService invoiceService = mock(InvoiceService.class);
  private final WorkerRepository workerRepository = mock(WorkerRepository.class);
  private final BankAccountRepository bankAccountRepository = mock(BankAccountRepository.class);
  private final MissionExecutionRepository missionExecutionRepository =
      mock(MissionExecutionRepository.class);
  private final FinancialPlanService service =
      new FinancialPlanService(
          contractRepository,
          invoiceService,
          workerRepository,
          bankAccountRepository,
          missionExecutionRepository);

  @Test
  void financialPlan_with_empty_data() {
    when(contractRepository.findByYear(2025)).thenReturn(List.of());
    when(workerRepository.findAll()).thenReturn(List.of());
    when(contractRepository.findAll()).thenReturn(List.of());
    when(bankAccountRepository.findAll()).thenReturn(List.of());
    when(missionExecutionRepository.missionExecutionsByDateBetweenAllWorkers(any(), any()))
        .thenReturn(List.of());

    var result = service.financialPlan(2025);

    assertNotNull(result);
    assertEquals(12, result.plannedCost().size());
    assertEquals(12, result.executedCost().size());
    assertTrue(result.koContracts().isEmpty());
  }
}
