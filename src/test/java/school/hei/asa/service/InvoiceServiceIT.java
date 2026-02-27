package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.BankAccount;
import school.hei.asa.model.InvoiceForm;
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
            List.of(),
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
            YearMonth.of(2025, Month.JANUARY),
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
    invoiceService.saveInvoiceReference(invoiceData, worker);

    var expected = invoiceService.generateInvoiceFileName(worker);

    var actual = invoiceService.getInvoiceBucketKey(worker, YearMonth.of(2025, Month.JANUARY));

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
    var actual = invoiceService.extractInvoiceData(newWorker(), invoiceForm);

    assertEquals(expected, actual);
  }

  private Worker newWorker() {
    return new Worker("w-code", "name", "email", "fullname", "address", "city", "nif", "stat");
  }
}
