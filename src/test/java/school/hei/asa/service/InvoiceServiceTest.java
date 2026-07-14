package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.model.*;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.model.contract.ContractType;
import school.hei.asa.number.NumberConverter;
import school.hei.asa.number.NumberParser;
import school.hei.asa.repository.*;

class InvoiceServiceTest {

  private final NumberConverter numberConverter = new NumberConverter();
  private final NumberParser numberParser = new NumberParser();
  private final ContractRepository contractRepository = mock(ContractRepository.class);
  private final MissionExecutionRepository missionExecutionRepository =
      mock(MissionExecutionRepository.class);
  private final BankAccountRepository bankAccountRepository = mock(BankAccountRepository.class);
  private final InvoiceReferenceRepository invoiceReferenceRepository =
      mock(InvoiceReferenceRepository.class);
  private final MissionService missionService = mock(MissionService.class);
  private final EventProducer eventProducer = mock(EventProducer.class);
  private final InvoiceService invoiceService =
      new InvoiceService(
          numberConverter,
          numberParser,
          contractRepository,
          missionExecutionRepository,
          bankAccountRepository,
          invoiceReferenceRepository,
          missionService,
          eventProducer);

  private Worker worker;
  private Contract contract;

  @BeforeEach
  void setUp() {
    worker =
        new Worker("W-001", "John", "john@test.com", "John Doe", "Addr", "City", "NIF", "STAT");
    contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("STD", ContractType.studentContractor, null, 25_000.0),
            Instant.parse("2025-01-01T00:00:00Z"),
            null,
            java.time.Duration.ofDays(100),
            "Company",
            null);
  }

  @Test
  void findInvoiceReference_found() {
    var ref = new InvoiceReference("id1", YearMonth.of(2025, 1), 1, worker);
    when(invoiceReferenceRepository.findInvoiceReferenceByWorker(worker)).thenReturn(List.of(ref));

    var result = invoiceService.findInvoiceReference(worker, YearMonth.of(2025, 1));

    assertTrue(result.isPresent());
    assertEquals("id1", result.get().id());
  }

  @Test
  void findInvoiceReference_notFound() {
    when(invoiceReferenceRepository.findInvoiceReferenceByWorker(worker)).thenReturn(List.of());

    var result = invoiceService.findInvoiceReference(worker, YearMonth.of(2025, 1));

    assertTrue(result.isEmpty());
  }

  @Test
  void saveInvoiceReference() {
    var invoiceForm =
        new InvoiceForm(
            "id1",
            YearMonth.of(2025, 1),
            LocalDate.now(),
            LocalDate.now(),
            "Desc",
            1.0,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    invoiceService.saveInvoiceReference(invoiceForm, worker);

    verify(invoiceReferenceRepository).saveInvoiceReference(any());
  }

  @Test
  void getInvoiceReference_found() {
    var ref = new InvoiceReference("id1", YearMonth.of(2025, 1), 1, worker);
    when(invoiceReferenceRepository.findInvoiceReferenceByInvoiceId("id1"))
        .thenReturn(Optional.of(ref));

    var result = invoiceService.getInvoiceReference("id1");

    assertEquals("id1", result.id());
  }

  @Test
  void getInvoiceReference_notFound_throws() {
    when(invoiceReferenceRepository.findInvoiceReferenceByInvoiceId("id1"))
        .thenReturn(Optional.empty());

    assertThrows(
        java.util.NoSuchElementException.class, () -> invoiceService.getInvoiceReference("id1"));
  }

  @Test
  void sendGenerateInvoiceEvent() {
    invoiceService.sendGenerateInvoiceEvent("invoice-123");

    verify(eventProducer).accept(any());
  }

  @Test
  void extractInvoiceData_without_contract() {
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    var invoiceForm =
        new InvoiceForm(
            "id1",
            YearMonth.of(2025, 1),
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    var result = invoiceService.extractInvoiceData(invoiceForm, List.of(), List.of(), null);

    assertNotNull(result);
    assertEquals(YearMonth.of(2025, 1), result.yearMonth());
  }

  @Test
  void generateInvoiceFileName_returnsProperFilename() {
    var ref = new InvoiceReference("id1", YearMonth.of(2025, 1), 5, worker);
    when(invoiceReferenceRepository.findInvoiceReferenceByWorker(worker)).thenReturn(List.of(ref));

    var result = invoiceService.generateInvoiceFileName(worker);

    assertEquals("FAC-NUM-2025-W-001-5.pdf", result);
  }

  @Test
  void extractInvoiceData_with_contract_and_missions() {
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    var product = new Product("P-001", "Product", "Desc");
    var mission = new Mission("M-001", "Mission", "Desc", 10, product);
    var execution =
        new MissionExecution(mission, worker, LocalDate.of(2025, 1, 15), 0.5, null, Instant.now());

    var invoiceForm =
        new InvoiceForm(
            "id1",
            YearMonth.of(2025, 1),
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    var result =
        invoiceService.extractInvoiceData(invoiceForm, List.of(contract), List.of(execution), null);

    assertNotNull(result);
    assertEquals("Job", result.description());
    assertEquals(0.5, result.quantity());
    assertNotNull(result.amount());
    assertFalse(result.hasUpgradedLevel());
  }

  @Test
  void extractInvoiceData_with_bank_account() {
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    var bankAccount = new BankAccount("BNI", "AG-001", "ACC-001", "KEY-001", "IBAN-001", worker);

    var invoiceForm =
        new InvoiceForm(
            "id1",
            YearMonth.of(2025, 1),
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    var result = invoiceService.extractInvoiceData(invoiceForm, List.of(), List.of(), bankAccount);

    assertNotNull(result);
    assertEquals(bankAccount.toString(), result.rib());
  }

  @Test
  void extractInvoiceData_with_upgraded_level() {
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    var contract1 =
        new Contract(
            worker,
            "Junior",
            new ContractLevel("JNR", ContractType.studentContractor, null, 20_000.0),
            Instant.parse("2025-01-15T00:00:00Z"),
            null,
            java.time.Duration.ofDays(50),
            "Company",
            null);
    var contract2 =
        new Contract(
            worker,
            "Senior",
            new ContractLevel("SNR", ContractType.studentContractor, null, 30_000.0),
            Instant.parse("2025-01-01T00:00:00Z"),
            null,
            java.time.Duration.ofDays(50),
            "Company",
            null);

    var product = new Product("P-001", "Product", "Desc");
    var mission = new Mission("M-001", "Mission", "Desc", 10, product);
    var execution1 =
        new MissionExecution(mission, worker, LocalDate.of(2025, 1, 10), 0.3, null, Instant.now());
    var execution2 =
        new MissionExecution(mission, worker, LocalDate.of(2025, 1, 20), 0.7, null, Instant.now());

    var invoiceForm =
        new InvoiceForm(
            "id1",
            YearMonth.of(2025, 1),
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    var result =
        invoiceService.extractInvoiceData(
            invoiceForm, List.of(contract1, contract2), List.of(execution1, execution2), null);

    assertNotNull(result);
    assertTrue(result.hasUpgradedLevel());
    assertNotNull(result.extraDescription());
    assertNotNull(result.total());
  }

  @Test
  void getInvoiceBucketKey() {
    var ref = new InvoiceReference("id1", YearMonth.of(2025, 1), 3, worker);
    when(invoiceReferenceRepository.findInvoiceReferenceByWorker(worker)).thenReturn(List.of(ref));

    var result = invoiceService.getInvoiceBucketKey(worker, YearMonth.of(2025, 1));

    assertEquals("FAC-NUM-2025-W-001-3.pdf", result);
  }

  @Test
  void extractInvoiceForm_with_contract() {
    when(contractRepository.findAllByWorker(worker)).thenReturn(List.of(contract));
    when(bankAccountRepository.findByWorkerCode(worker.code())).thenReturn(null);
    when(missionExecutionRepository.missionExecutionsByDateBetween(any(), any(), any()))
        .thenReturn(List.of());
    when(missionService.isUnpaidCare(any())).thenReturn(false);

    var invoiceForm =
        new InvoiceForm(
            "id1", null, null, null, null, null, null, null, false, null, null, null, null, null,
            null, null);

    var result = invoiceService.extractInvoiceForm(worker, invoiceForm);

    assertNotNull(result);
    assertEquals(YearMonth.from(LocalDate.now()), result.yearMonth());
    verify(contractRepository).findAllByWorker(worker);
    verify(bankAccountRepository).findByWorkerCode(worker.code());
    verify(missionExecutionRepository).missionExecutionsByDateBetween(any(), any(), any());
  }
}
