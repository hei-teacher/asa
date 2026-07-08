package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static school.hei.asa.model.contract.ContractType.studentContractor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
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
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.repository.ContractRepository;

public class InvoiceServiceIT extends FacadeIT {

  @Autowired InvoiceService invoiceService;
  @MockBean ContractRepository contractRepository;
  @MockBean BankAccountRepository bankAccountRepository;

  @BeforeEach
  void setUp() {
    var contract =
        new Contract(
            newWorker(),
            "job",
            new ContractLevel("code", studentContractor, null, 55_556d),
            Instant.now(),
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
            YearMonth.of(
                2025, Month.MAY), // 💡 Changé en MAI pour éviter le conflit avec l'ID 1 de Janvier
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
    var worker = new Worker("W-P-2024-01", "Lita Andria", "", "", "", "", "", "");

    // Sauvegarde réelle en base de données
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
            new ContractLevel("L2", studentContractor, null, 80_000d),
            Instant.parse("2026-01-15T00:00:00Z"),
            null,
            Duration.ofDays(30),
            "company",
            "");
    var contractOld =
        new Contract(
            worker,
            "Junior Dev",
            new ContractLevel("L1", studentContractor, null, 50_000d),
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
            new ContractLevel("L1", studentContractor, null, 60_000d),
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
    assertEquals(
        invoiceId, actual.id()); // Remplace par ta méthode de récupération d'ID (ex: getId())
  }

  @Test
  void getInvoiceReference_should_throw_NoSuchElementException_when_not_found() {
    String invoiceId = "inv-completely-unknown";

    var exception =
        assertThrows(
            NoSuchElementException.class, () -> invoiceService.getInvoiceReference(invoiceId));

    assertEquals("Invoice reference not found", exception.getMessage());
  }

  private Worker newWorker() {
    return new Worker("w-code", "name", "email", "fullname", "address", "city", "nif", "stat");
  }
}
