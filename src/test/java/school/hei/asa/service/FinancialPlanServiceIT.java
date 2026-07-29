package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.hei.asa.model.contract.ContractType.fullTimeEmployee;
import static school.hei.asa.model.contract.cas.ContractToCasTest.JAN1_2026;
import static school.hei.asa.model.contract.cas.ContractToCasTest.studentContract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
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

  private static final double FTE_MONTHLY_PAY = 300_000d;

  @Test
  void generate_financial_plan_successfully() {
    int testYear = 2026;
    var yearStart = LocalDate.of(testYear, 1, 1);
    var yearEnd = LocalDate.of(testYear, 12, 31);

    var missionWorker =
        new Worker(
            "W-101",
            "Test Worker",
            "worker@example.com",
            "Full Worker Name",
            "address",
            "random city",
            "nif",
            "stat");

    var fteWorker =
        new Worker(
            "W-102",
            "FTE Worker",
            "fte.worker@example.com",
            "Full Time Worker Name",
            "address",
            "random city",
            "nif",
            "stat");

    var fteContract =
        new Contract(
            fteWorker,
            "jobTitle",
            new ContractLevel("level", fullTimeEmployee, FTE_MONTHLY_PAY, null),
            JAN1_2026.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            null,
            null,
            "company",
            "contractBucketKey");

    when(contractRepository.findByYear(testYear))
        .thenReturn(List.of(studentContract(JAN1_2026, 11, 50_000)));
    when(workerRepository.findAll()).thenReturn(List.of(missionWorker, fteWorker));
    when(contractRepository.findAll()).thenReturn(List.of(fteContract));
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
                  BigDecimal.valueOf(2000),
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

    assertEquals("302000.0", financialPlan.executedCost().get(Month.JANUARY).ppMontant().trim());
    assertEquals("302000.0", financialPlan.executedCost().get(Month.DECEMBER).ppMontant().trim());
    assertEquals(12, financialPlan.executedCost().size());

    assertTrue(financialPlan.koContracts().isEmpty());

    verify(contractRepository).findByYear(testYear);
    verify(workerRepository).findAll();
    verify(missionExecutionRepository).missionExecutionsByDateBetweenAllWorkers(yearStart, yearEnd);
    verify(invoiceService, org.mockito.Mockito.times(12))
        .extractInvoiceData(any(), any(), any(), any());
  }
}
