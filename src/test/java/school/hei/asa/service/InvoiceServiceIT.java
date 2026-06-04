package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.hei.asa.model.contract.ContractType.studentContractor;

import java.io.File;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import school.hei.asa.repository.InvoiceReferenceRepository;

public class InvoiceServiceIT extends FacadeIT {
  @Autowired InvoiceService invoiceService;

  @MockBean ContractRepository contractRepository;

  @MockBean BankAccountRepository bankAccountRepository;
  @MockBean PDFScrapper pdfScrapper;

  @MockBean InvoiceReferenceRepository invoiceReferenceRepository;

  private static final String INVOICES_FOLDER = "invoices/";

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

  @Test
  void can_download_yearmonth_invoices() {
    InvoiceService spyService = Mockito.spy(invoiceService);

    doReturn(List.of()).when(spyService).downloadInvoiceByYearMonth(YearMonth.of(2026, 6));
    spyService.downloadInvoiceByYearMonth(YearMonth.of(2026, 6));
    verify(spyService, times(1)).downloadInvoiceByYearMonth(YearMonth.of(2026, 6));
  }

  @Test
  void can_get_invoice_total_amount_by_month() {
    InvoiceService spyService = Mockito.spy(invoiceService);

    var yearMonth = YearMonth.of(2026, 6);
    var mockFile1 = new File("FAC-NUM-2025-W001-1.pdf");
    var mockFile2 = new File("FAC-NUM-2025-W002-2.pdf");
    var mockFiles = List.of(mockFile1, mockFile2);

    doReturn(mockFiles).when(spyService).downloadInvoiceByYearMonth(yearMonth);
    when(pdfScrapper.extractTotalAmount(mockFile1)).thenReturn(55_000L);
    when(pdfScrapper.extractTotalAmount(mockFile2)).thenReturn(45_000L);

    var actual = spyService.getInvoiceTotalAmountByMonth(yearMonth);

    assertEquals(100_000L, actual);
    verify(spyService, times(1)).downloadInvoiceByYearMonth(yearMonth);
    verify(pdfScrapper, times(1)).extractTotalAmount(mockFile1);
    verify(pdfScrapper, times(1)).extractTotalAmount(mockFile2);
  }
}
