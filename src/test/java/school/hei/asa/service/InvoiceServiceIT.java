package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static school.hei.asa.model.contract.ContractType.fullTimeEmployee;
import static school.hei.asa.model.contract.ContractType.studentContractor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.BankAccount;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.WorkerRepository;

public class InvoiceServiceIT extends FacadeIT {

  @Autowired InvoiceService invoiceService;
  @Autowired WorkerRepository workerRepository;
  @MockBean ContractRepository contractRepository;
  @MockBean BankAccountRepository bankAccountRepository;

  @BeforeEach
  void setUp() {
    var contract =
        new Contract(
            newWorker(),
            "job",
            new ContractLevel("code", studentContractor, null, 55_556d, null),
            Instant.parse("2025-01-01T08:00:00Z"),
            null,
            Duration.ofDays(100),
            "company",
            "");
    when(contractRepository.findAllByWorker(any())).thenReturn(List.of(contract));
    when(bankAccountRepository.findByWorkerCode(anyString()))
        .thenReturn(new BankAccount("", "", "", "", "", newWorker()));
  }

  @Test
  void can_generate_invoice_bucketKey() {
    var invoiceData =
        new InvoiceForm(
            "some id",
            YearMonth.of(2025, Month.MAY),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    // own worker: invoice references saved here must not leak into InvoiceReferenceRepositoryTest
    var worker = new Worker("W-INVOICE-IT", "Invoice IT", "", "", "", "", "", "", null, null);
    workerRepository.save(worker);

    invoiceService.saveInvoiceReference(invoiceData, worker);

    var expected = invoiceService.generateInvoiceFileName(worker);
    var actual = invoiceService.getInvoiceBucketKey(worker, YearMonth.of(2025, Month.MAY));

    assertEquals(expected, actual);
  }

  @Test
  void can_generate_invoiceForm() {
    var invoiceForm =
        new InvoiceForm(
            "id",
            YearMonth.of(2025, 1),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    var expected =
        new InvoiceForm(
            "id",
            YearMonth.of(2025, 1),
            LocalDate.now(),
            LocalDate.now().plusDays(3),
            "job",
            0.0d,
            BigDecimal.valueOf(55_556.0d),
            BigDecimal.valueOf(0.0d),
            false,
            null,
            null,
            null,
            null,
            BigDecimal.valueOf(0.0d),
            "Zéro",
            "Banque: , Agence: , Compte: , Clé: , IBAN: ");
    var actual = invoiceService.extractInvoiceForm(newWorker(), invoiceForm);

    assertEquals(expected, actual);
  }

  @Test
  void extractInvoiceData_with_upgraded_level_should_combine_contracts() {
    var worker = newWorker();
    var targetMonth = YearMonth.of(2026, Month.JANUARY);
    var invoiceForm =
        new InvoiceForm(
            "inv-upgrade",
            targetMonth,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    var contractUpgraded =
        new Contract(
            worker,
            "Senior Dev",
            new ContractLevel("L2", studentContractor, null, 80_000d, null),
            Instant.parse("2026-01-15T00:00:00Z"),
            null,
            Duration.ofDays(30),
            "company",
            "");
    var contractOld =
        new Contract(
            worker,
            "Junior Dev",
            new ContractLevel("L1", studentContractor, null, 50_000d, null),
            Instant.parse("2025-01-01T00:00:00Z"),
            null,
            Duration.ofDays(365),
            "company",
            "");

    var bankAccount = new BankAccount("B1", "A1", "C1", "K1", "IBAN1", worker);
    var result =
        invoiceService.extractInvoiceData(
            invoiceForm, List.of(contractUpgraded, contractOld), List.of(), bankAccount);

    assertNotNull(result);
    assertEquals("inv-upgrade", result.id());
    assertEquals(targetMonth, result.yearMonth());
    assertTrue(result.hasUpgradedLevel());
    assertNotNull(result.total());
    assertNotNull(result.parsedAmount());
  }

  @Test
  void extractInvoiceData_with_upgraded_level_should_price_each_period_with_its_own_contract() {
    var worker = newWorker();
    var invoiceForm =
        new InvoiceForm(
            "inv-periods",
            YearMonth.of(2026, Month.JANUARY),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    var newContract =
        new Contract(
            worker,
            "Senior Dev",
            new ContractLevel("L2", studentContractor, null, 80_000d, null),
            Instant.parse("2026-01-15T00:00:00Z"),
            null,
            Duration.ofDays(30),
            "company",
            "");
    var oldContract =
        new Contract(
            worker,
            "Junior Dev",
            new ContractLevel("L1", studentContractor, null, 50_000d, null),
            Instant.parse("2025-01-01T00:00:00Z"),
            null,
            Duration.ofDays(365),
            "company",
            "");
    var product = new Product("p-code", "p-name", "p-description");
    var mission = new Mission("m-code", "m-title", "m-description", 0, product);
    var executions =
        List.of(
            execution(mission, worker, LocalDate.of(2026, 1, 10)),
            execution(mission, worker, LocalDate.of(2026, 1, 14)),
            execution(mission, worker, LocalDate.of(2026, 1, 15)),
            execution(mission, worker, LocalDate.of(2026, 1, 20)),
            execution(mission, worker, LocalDate.of(2026, 1, 31)));
    var bankAccount = new BankAccount("B1", "A1", "C1", "K1", "IBAN1", worker);

    var actual =
        invoiceService.extractInvoiceData(
            invoiceForm, List.of(newContract, oldContract), executions, bankAccount);

    assertTrue(actual.hasUpgradedLevel());
    assertEquals("Junior Dev", actual.description());
    assertEquals(2d, actual.quantity());
    assertEquals(0, BigDecimal.valueOf(50_000).compareTo(actual.unitPrice()));
    assertEquals(0, BigDecimal.valueOf(100_000).compareTo(actual.amount()));
    assertEquals("Senior Dev", actual.extraDescription());
    assertEquals(3d, actual.extraQuantity());
    assertEquals(0, BigDecimal.valueOf(80_000).compareTo(actual.extraUnitPrice()));
    assertEquals(0, BigDecimal.valueOf(240_000).compareTo(actual.extraAmount()));
    assertEquals(0, BigDecimal.valueOf(340_000).compareTo(actual.total()));
  }

  private MissionExecution execution(Mission mission, Worker worker, LocalDate date) {
    return new MissionExecution(mission, worker, date, 1d, "comment", Instant.now());
  }

  @Test
  void extractInvoiceData_without_upgraded_level_should_process_normally() {
    var worker = newWorker();
    var targetMonth = YearMonth.of(2026, Month.JANUARY);
    var invoiceForm =
        new InvoiceForm(
            "inv-normal",
            targetMonth,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    var uniqueContract =
        new Contract(
            worker,
            "Dev",
            new ContractLevel("L1", studentContractor, null, 60_000d, null),
            Instant.parse("2025-01-01T00:00:00Z"),
            null,
            Duration.ofDays(365),
            "company",
            "");

    var bankAccount = new BankAccount("B1", "A1", "C1", "K1", "IBAN1", worker);

    var result =
        invoiceService.extractInvoiceData(
            invoiceForm, List.of(uniqueContract), List.of(), bankAccount);

    assertNotNull(result);
    assertEquals("inv-normal", result.id());
    assertFalse(result.hasUpgradedLevel());
  }

  @Test
  void getInvoiceReference_should_return_reference_when_found() {
    String invoiceId = "id1";

    InvoiceReference actual = invoiceService.getInvoiceReference(invoiceId);

    assertNotNull(actual);
    assertEquals(invoiceId, actual.id());
  }

  @Test
  void getInvoiceReference_should_throw_NoSuchElementException_when_not_found() {
    String invoiceId = "inv-completely-unknown";

    var exception =
        assertThrows(
            NoSuchElementException.class, () -> invoiceService.getInvoiceReference(invoiceId));

    assertEquals("Invoice reference not found", exception.getMessage());
  }

  @Test
  void extractInvoiceData_should_price_past_month_with_contract_of_that_month() {
    var worker = newWorker();
    var invoiceForm =
        new InvoiceForm(
            "inv-past",
            YearMonth.of(2025, Month.DECEMBER),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    var newContract =
        new Contract(
            worker,
            "Senior Dev",
            new ContractLevel("L2", studentContractor, null, 80_000d, null),
            Instant.parse("2026-01-01T08:00:00Z"),
            null,
            Duration.ofDays(30),
            "company",
            "");
    var oldContract =
        new Contract(
            worker,
            "Junior Dev",
            new ContractLevel("L1", studentContractor, null, 50_000d, null),
            Instant.parse("2025-01-01T08:00:00Z"),
            Instant.parse("2025-12-31T08:00:00Z"),
            Duration.ofDays(365),
            "company",
            "");
    var product = new Product("p-code", "p-name", "p-description");
    var mission = new Mission("m-code", "m-title", "m-description", 0, product);
    var executions =
        List.of(
            execution(mission, worker, LocalDate.of(2025, 12, 10)),
            execution(mission, worker, LocalDate.of(2025, 12, 11)));

    var actual =
        invoiceService.extractInvoiceData(
            invoiceForm, List.of(newContract, oldContract), executions, null);

    assertFalse(actual.hasUpgradedLevel());
    assertEquals("Junior Dev", actual.description());
    assertEquals(0, BigDecimal.valueOf(50_000).compareTo(actual.unitPrice()));
    assertEquals(0, BigDecimal.valueOf(100_000).compareTo(actual.total()));
  }

  @Test
  void extractInvoiceData_should_not_pay_executions_after_contract_end() {
    var worker = newWorker();
    var contract =
        dailyContract(worker, "Dev", 50_000d, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 10, 10));
    var mission = newMission();
    var executions =
        List.of(
            execution(mission, worker, LocalDate.of(2026, 10, 5)),
            execution(mission, worker, LocalDate.of(2026, 10, 10)),
            execution(mission, worker, LocalDate.of(2026, 10, 20)));

    var actual =
        invoiceService.extractInvoiceData(
            emptyInvoiceForm(YearMonth.of(2026, Month.OCTOBER)),
            List.of(contract),
            executions,
            null);

    assertFalse(actual.hasUpgradedLevel());
    assertEquals(2d, actual.quantity());
    assertEquals(0, BigDecimal.valueOf(100_000).compareTo(actual.total()));
  }

  @Test
  void extractInvoiceData_should_split_when_new_contract_starts_on_last_day_of_month() {
    var worker = newWorker();
    var oldContract = dailyContract(worker, "Junior Dev", 50_000d, LocalDate.of(2025, 1, 1), null);
    var newContract = dailyContract(worker, "Senior Dev", 80_000d, LocalDate.of(2026, 1, 31), null);
    var mission = newMission();
    var executions =
        List.of(
            execution(mission, worker, LocalDate.of(2026, 1, 30)),
            execution(mission, worker, LocalDate.of(2026, 1, 31)));

    var actual =
        invoiceService.extractInvoiceData(
            emptyInvoiceForm(YearMonth.of(2026, Month.JANUARY)),
            List.of(newContract, oldContract),
            executions,
            null);

    assertTrue(actual.hasUpgradedLevel());
    assertEquals(1d, actual.quantity());
    assertEquals(1d, actual.extraQuantity());
    assertEquals(0, BigDecimal.valueOf(130_000).compareTo(actual.total()));
  }

  @Test
  void extractInvoiceData_with_upgraded_level_should_not_pay_old_contract_after_its_end() {
    var worker = newWorker();
    var oldContract =
        dailyContract(
            worker, "Junior Dev", 50_000d, LocalDate.of(2025, 1, 1), LocalDate.of(2026, 1, 10));
    var newContract = dailyContract(worker, "Senior Dev", 80_000d, LocalDate.of(2026, 1, 20), null);
    var mission = newMission();
    var executions =
        List.of(
            execution(mission, worker, LocalDate.of(2026, 1, 5)),
            execution(mission, worker, LocalDate.of(2026, 1, 15)),
            execution(mission, worker, LocalDate.of(2026, 1, 25)));

    var actual =
        invoiceService.extractInvoiceData(
            emptyInvoiceForm(YearMonth.of(2026, Month.JANUARY)),
            List.of(newContract, oldContract),
            executions,
            null);

    assertTrue(actual.hasUpgradedLevel());
    assertEquals(1d, actual.quantity());
    assertEquals(1d, actual.extraQuantity());
    assertEquals(0, BigDecimal.valueOf(130_000).compareTo(actual.total()));
  }

  @Test
  void extractInvoiceData_should_pay_full_salary_for_full_month() {
    var worker = newWorker();
    var contract = salariedContract(worker, 310_000d, LocalDate.of(2025, 1, 1), null);

    var actual =
        invoiceService.extractInvoiceData(
            emptyInvoiceForm(YearMonth.of(2026, Month.MARCH)), List.of(contract), List.of(), null);

    assertEquals(1d, actual.quantity());
    assertEquals(0, BigDecimal.valueOf(310_000).compareTo(actual.total()));
  }

  @Test
  void extractInvoiceData_should_prorate_salary_over_calendar_days_covered() {
    var worker = newWorker();
    var contract = salariedContract(worker, 300_000d, LocalDate.of(2026, 6, 15), null);

    var actual =
        invoiceService.extractInvoiceData(
            emptyInvoiceForm(YearMonth.of(2026, Month.JUNE)), List.of(contract), List.of(), null);

    // 16 of 30 days
    assertEquals(0.53d, actual.quantity());
    assertEquals(0, BigDecimal.valueOf(160_000).compareTo(actual.total()));
  }

  @Test
  void extractInvoiceData_should_prorate_salary_when_switching_to_contractor_mid_month() {
    var worker = newWorker();
    var salaried =
        salariedContract(worker, 310_000d, LocalDate.of(2025, 1, 1), LocalDate.of(2026, 3, 14));
    var contractor = dailyContract(worker, "Dev", 50_000d, LocalDate.of(2026, 3, 15), null);
    var mission = newMission();
    var executions =
        List.of(
            execution(mission, worker, LocalDate.of(2026, 3, 16)),
            execution(mission, worker, LocalDate.of(2026, 3, 17)));

    var actual =
        invoiceService.extractInvoiceData(
            emptyInvoiceForm(YearMonth.of(2026, Month.MARCH)),
            List.of(contractor, salaried),
            executions,
            null);

    assertTrue(actual.hasUpgradedLevel());
    // 14 of 31 days of salary, then 2 days as contractor
    assertEquals(0, BigDecimal.valueOf(140_000).compareTo(actual.amount()));
    assertEquals(0, BigDecimal.valueOf(100_000).compareTo(actual.extraAmount()));
    assertEquals(0, BigDecimal.valueOf(240_000).compareTo(actual.total()));
  }

  @Test
  void extractInvoiceData_should_prorate_salary_when_switching_from_contractor_mid_month() {
    var worker = newWorker();
    var contractor =
        dailyContract(worker, "Dev", 50_000d, LocalDate.of(2025, 1, 1), LocalDate.of(2026, 3, 14));
    var salaried = salariedContract(worker, 310_000d, LocalDate.of(2026, 3, 15), null);
    var mission = newMission();
    var executions =
        List.of(
            execution(mission, worker, LocalDate.of(2026, 3, 2)),
            execution(mission, worker, LocalDate.of(2026, 3, 3)));

    var actual =
        invoiceService.extractInvoiceData(
            emptyInvoiceForm(YearMonth.of(2026, Month.MARCH)),
            List.of(salaried, contractor),
            executions,
            null);

    assertTrue(actual.hasUpgradedLevel());
    // 2 days as contractor, then 17 of 31 days of salary
    assertEquals(0, BigDecimal.valueOf(100_000).compareTo(actual.amount()));
    assertEquals(0, BigDecimal.valueOf(170_000).compareTo(actual.extraAmount()));
    assertEquals(0, BigDecimal.valueOf(270_000).compareTo(actual.total()));
  }

  private static Contract salariedContract(
      Worker worker, double monthlyPay, LocalDate start, LocalDate end) {
    return new Contract(
        worker,
        "Salaried",
        new ContractLevel("S", fullTimeEmployee, monthlyPay, null, null),
        start.atTime(8, 0).atZone(ZoneId.systemDefault()).toInstant(),
        end == null ? null : end.atStartOfDay(ZoneId.systemDefault()).toInstant(),
        null,
        "company",
        "");
  }

  private static Contract dailyContract(
      Worker worker, String jobTitle, double dailyPay, LocalDate start, LocalDate end) {
    return new Contract(
        worker,
        jobTitle,
        new ContractLevel(jobTitle, studentContractor, null, dailyPay, null),
        start.atTime(8, 0).atZone(ZoneId.systemDefault()).toInstant(),
        end == null ? null : end.atStartOfDay(ZoneId.systemDefault()).toInstant(),
        Duration.ofDays(365),
        "company",
        "");
  }

  private static Mission newMission() {
    var product = new Product("p-code", "p-name", "p-description");
    return new Mission("m-code", "m-title", "m-description", 0, product);
  }

  private static InvoiceForm emptyInvoiceForm(YearMonth yearMonth) {
    return new InvoiceForm(
        "inv", yearMonth, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null);
  }

  private Worker newWorker() {
    return new Worker(
        "w-code", "name", "email", "fullname", "address", "city", "nif", "stat", null, null);
  }
}
