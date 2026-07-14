package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.model.*;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.model.contract.ContractType;
import school.hei.asa.number.NumberConverter;
import school.hei.asa.number.NumberParser;
import school.hei.asa.repository.*;

class InvoiceServiceConcreteTest {

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
  private final InvoiceService service =
      new InvoiceService(
          numberConverter,
          numberParser,
          contractRepository,
          missionExecutionRepository,
          bankAccountRepository,
          invoiceReferenceRepository,
          missionService,
          eventProducer);

  private final Worker worker =
      new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

  @Test
  void extractInvoiceData_no_contract() {
    when(missionService.isUnpaidCare(any())).thenReturn(false);
    var form =
        new InvoiceForm(
            "id1",
            YearMonth.of(2025, 6),
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

    var result = service.extractInvoiceData(form, List.of(), List.of(), null);

    assertEquals(YearMonth.of(2025, 6), result.yearMonth());
    assertNotNull(result.referenceDate());
    assertNotNull(result.issueDate());
    assertFalse(result.hasUpgradedLevel());
    assertNull(result.description());
  }

  @Test
  void extractInvoiceData_with_contract_partnerContractor() {
    when(missionService.isUnpaidCare(any())).thenReturn(false);
    var level = new ContractLevel("STD", ContractType.partnerContractor, null, 50_000.0);
    var contract =
        new Contract(
            worker,
            "Dev",
            level,
            Instant.parse("2025-01-01T00:00:00Z"),
            null,
            Duration.ofDays(365),
            "Company",
            null);
    var mission = new Mission("M001", "M", "D", 10, new Product("P1", "P", "D"));
    var me =
        new MissionExecution(mission, worker, LocalDate.of(2025, 6, 1), 0.5, "c", Instant.now());
    var form =
        new InvoiceForm(
            "id1",
            YearMonth.of(2025, 6),
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

    var result = service.extractInvoiceData(form, List.of(contract), List.of(me), null);

    assertFalse(result.hasUpgradedLevel());
    assertEquals("Dev", result.description());
    assertEquals(0.5, result.quantity());
    assertNotNull(result.unitPrice());
  }

  @Test
  void extractInvoiceData_with_upgraded_level() {
    when(missionService.isUnpaidCare(any())).thenReturn(false);
    var oldLevel = new ContractLevel("OLD", ContractType.partnerContractor, null, 30_000.0);
    var newLevel = new ContractLevel("NEW", ContractType.partnerContractor, null, 50_000.0);
    var oldContract =
        new Contract(
            worker,
            "Junior",
            oldLevel,
            Instant.parse("2025-01-01T00:00:00Z"),
            null,
            Duration.ofDays(180),
            "Company",
            null);
    var newContract =
        new Contract(
            worker,
            "Senior",
            newLevel,
            Instant.parse("2025-06-15T00:00:00Z"),
            null,
            Duration.ofDays(180),
            "Company",
            null);
    var mission = new Mission("M001", "M", "D", 10, new Product("P1", "P", "D"));
    var me =
        new MissionExecution(mission, worker, LocalDate.of(2025, 6, 20), 1.0, "c", Instant.now());
    var form =
        new InvoiceForm(
            "id1",
            YearMonth.of(2025, 6),
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
        service.extractInvoiceData(form, List.of(newContract, oldContract), List.of(me), null);

    assertTrue(result.hasUpgradedLevel());
    assertNotNull(result.total());
    assertTrue(result.total().compareTo(BigDecimal.ZERO) > 0);
  }

  @Test
  void extractInvoiceData_empty_yearMonth_uses_current() {
    when(missionService.isUnpaidCare(any())).thenReturn(false);
    var form =
        new InvoiceForm(
            "id1", null, null, null, null, null, null, null, false, null, null, null, null, null,
            null, null);

    var result = service.extractInvoiceData(form, List.of(), List.of(), null);

    assertEquals(YearMonth.from(LocalDate.now()), result.yearMonth());
  }

  @Test
  void generateInvoiceFileName_with_references() {
    when(invoiceReferenceRepository.findInvoiceReferenceByWorker(worker))
        .thenReturn(List.of(new InvoiceReference("id1", YearMonth.of(2025, 1), 3, worker)));

    var result = service.generateInvoiceFileName(worker);

    assertEquals("FAC-NUM-2025-W-001-3.pdf", result);
  }

  @Test
  void getInvoiceBucketKey_with_matching_ref() {
    var ref1 = new InvoiceReference("id1", YearMonth.of(2025, 1), 1, worker);
    var ref2 = new InvoiceReference("id2", YearMonth.of(2025, 6), 2, worker);
    when(invoiceReferenceRepository.findInvoiceReferenceByWorker(worker))
        .thenReturn(List.of(ref1, ref2));

    var result = service.getInvoiceBucketKey(worker, YearMonth.of(2025, 6));

    assertEquals("FAC-NUM-2025-W-001-2.pdf", result);
  }

  @Test
  void saveInvoiceReference_saves() {
    var form =
        new InvoiceForm(
            "id1",
            YearMonth.of(2025, 6),
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

    service.saveInvoiceReference(form, worker);

    verify(invoiceReferenceRepository).saveInvoiceReference(any());
  }

  @Test
  void sendGenerateInvoiceEvent_fires_event() {
    service.sendGenerateInvoiceEvent("inv-123");

    verify(eventProducer).accept(any());
  }
}
