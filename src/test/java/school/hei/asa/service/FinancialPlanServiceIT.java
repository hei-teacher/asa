package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.hei.asa.model.contract.cas.ContractToCasTest.JAN1_2026;
import static school.hei.asa.model.contract.cas.ContractToCasTest.studentContract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

class FinancialPlanServiceIT extends FacadeIT {

  @Autowired FinancialPlanService subject;

  @MockBean ContractRepository contractRepository;
  @MockBean InvoiceService invoiceService;
  @MockBean WorkerRepository workerRepository;
  @MockBean BankAccountRepository bankAccountRepository;
  @MockBean MissionExecutionRepository missionExecutionRepository;

  @Test
  void generate_financial_plan_successfully() {
    int testYear = 2026;
    var yearStart = LocalDate.of(testYear, 1, 1);
    var yearEnd = LocalDate.of(testYear, 12, 31);

    var worker =
        new Worker(
            "W-101",
            "Test Worker",
            "worker@example.com",
            "Full Worker Name",
            "address",
            "random city",
            "nif",
            "stat");

    when(contractRepository.findByYear(testYear))
        .thenReturn(List.of(studentContract(JAN1_2026, 11, 50_000)));
    when(workerRepository.findAll()).thenReturn(List.of(worker));
    when(contractRepository.findAll()).thenReturn(List.of());
    when(bankAccountRepository.findAll()).thenReturn(List.of());
    when(missionExecutionRepository.missionExecutionsByDateBetweenAllWorkers(yearStart, yearEnd))
        .thenReturn(List.of());

    when(invoiceService.extractInvoiceData(any(), any(), any(), any()))
        .thenAnswer(
            invocation -> {
              InvoiceForm passedForm = invocation.getArgument(0);
              return new InvoiceForm(
                  passedForm.id(),
                  passedForm.yearMonth(),
                  LocalDate.now(),
                  LocalDate.now().plusDays(3),
                  "job",
                  0.0d,
                  BigDecimal.valueOf(2000), // Montant exécuté simulé
                  BigDecimal.valueOf(2000),
                  false,
                  null,
                  null,
                  null,
                  null,
                  BigDecimal.valueOf(2000),
                  "Zéro",
                  "Banque: Multi");
            });

    var financialPlan = subject.financialPlan(testYear);

    assertNotNull(financialPlan);
    assertEquals("550000.0", financialPlan.plannedCost().get(Month.FEBRUARY).ppMontant().trim());
    assertEquals("0.0", financialPlan.plannedCost().get(Month.JANUARY).ppMontant().trim());
    assertEquals("2000.0", financialPlan.executedCost().get(Month.JANUARY).ppMontant().trim());
    assertEquals("2000.0", financialPlan.executedCost().get(Month.DECEMBER).ppMontant().trim());
    assertEquals(12, financialPlan.executedCost().size());

    assertTrue(financialPlan.koContracts().isEmpty());

    verify(contractRepository).findByYear(testYear);
    verify(workerRepository).findAll();
    verify(missionExecutionRepository).missionExecutionsByDateBetweenAllWorkers(yearStart, yearEnd);
  }
}
