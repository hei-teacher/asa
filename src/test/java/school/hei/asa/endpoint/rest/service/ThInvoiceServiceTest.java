package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import school.hei.asa.endpoint.rest.controller.mapper.ThInvoiceFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.Worker;
import school.hei.asa.service.InvoiceService;

class ThInvoiceServiceTest {

  private final InvoiceService invoiceService = mock(InvoiceService.class);
  private final ThInvoiceFormMapper thInvoiceFormMapper = mock(ThInvoiceFormMapper.class);
  private final InvoicePDFGenerator invoicePDFGenerator = mock(InvoicePDFGenerator.class);
  private final ThInvoiceService subject =
      new ThInvoiceService(invoiceService, thInvoiceFormMapper, invoicePDFGenerator);

  private final Worker worker =
      new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

  @BeforeEach
  void setUp() {
    reset(invoiceService, thInvoiceFormMapper, invoicePDFGenerator);
  }

  @Test
  void generateInvoiceFileName_delegates_to_invoiceService() {
    var expected = "FAC-NUM-2025-W-001-42.pdf";
    when(invoiceService.generateInvoiceFileName(worker)).thenReturn(expected);

    var actual = subject.generateInvoiceFileName(worker);

    assertEquals(expected, actual);
    verify(invoiceService).generateInvoiceFileName(worker);
  }

  @Test
  void saveInvoiceReference_calls_toDomain_and_saveInvoiceReference() {
    var thInvoiceForm =
        new ThInvoiceForm(
            "id",
            "2025-01",
            "01/01/2025",
            "03/01/2025",
            "Dev",
            "20.0",
            "50000",
            "1000000",
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    var invoiceData = mock(InvoiceForm.class);
    when(thInvoiceFormMapper.toDomain(thInvoiceForm)).thenReturn(invoiceData);

    subject.saveInvoiceReference(thInvoiceForm, worker);

    verify(thInvoiceFormMapper).toDomain(thInvoiceForm);
    verify(invoiceService).saveInvoiceReference(invoiceData, worker);
  }

  @Test
  void getMonthInvoiceStatusForWorker_returns_false_when_no_reference() {
    when(invoiceService.findInvoiceReference(eq(worker), any(YearMonth.class)))
        .thenReturn(Optional.empty());

    var result = subject.getMonthInvoiceStatusForWorker(worker, 2025);

    assertEquals(12, result.size());
    result.forEach(status -> assertFalse(status.hasInvoice()));
  }

  @Test
  void getMonthInvoiceStatusForWorker_returns_true_for_present_references() {
    for (var m : Month.values()) {
      var ym = YearMonth.of(2025, m);
      if (m == Month.JANUARY || m == Month.FEBRUARY) {
        when(invoiceService.findInvoiceReference(eq(worker), eq(ym)))
            .thenReturn(
                Optional.of(new InvoiceReference("inv-" + m.getValue(), ym, m.getValue(), worker)));
      } else {
        when(invoiceService.findInvoiceReference(eq(worker), eq(ym))).thenReturn(Optional.empty());
      }
    }

    var result = subject.getMonthInvoiceStatusForWorker(worker, 2025);

    assertEquals(12, result.size());
    for (var status : result) {
      if (status.yearMonth().equals(YearMonth.of(2025, Month.JANUARY))
          || status.yearMonth().equals(YearMonth.of(2025, Month.FEBRUARY))) {
        assertTrue(status.hasInvoice(), "expected invoice for " + status.yearMonth());
      } else {
        assertFalse(status.hasInvoice(), "expected no invoice for " + status.yearMonth());
      }
    }
  }

  @Test
  void extractInvoice_happy_path(@TempDir File tempDir) throws Exception {
    var invoiceForm =
        new ThInvoiceForm(
            "id",
            "2025-01",
            "01/01/2025",
            "03/01/2025",
            "Dev",
            "20.0",
            "50000",
            "1000000",
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    var domainInvoiceForm = mock(InvoiceForm.class);
    var extractedInvoiceData = mock(InvoiceForm.class);
    var thInvoiceData =
        new ThInvoiceForm(
            "th-id",
            "2025-01",
            "01/01/2025",
            "03/01/2025",
            "Dev",
            "20.0",
            "50000",
            "1000000",
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    when(thInvoiceFormMapper.toDomain(invoiceForm)).thenReturn(domainInvoiceForm);
    when(invoiceService.extractInvoiceForm(worker, domainInvoiceForm))
        .thenReturn(extractedInvoiceData);
    when(thInvoiceFormMapper.toTh(extractedInvoiceData)).thenReturn(thInvoiceData);

    var pdfFile = new File(tempDir, "invoice.pdf");
    var pdfBytes =
        "%PDF-1.4\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n3 0 obj<</Type/Page/MediaBox[0 0 612 792]/Parent 2 0 R>>endobj\nxref\n0 4\n0000000000 65535 f \n0000000009 00000 n \n0000000058 00000 n \n0000000115 00000 n \ntrailer<</Size 4/Root 1 0 R>>\nstartxref\n190\n%%EOF"
            .getBytes(StandardCharsets.UTF_8);
    Files.write(pdfFile.toPath(), pdfBytes);

    when(invoicePDFGenerator.apply(worker, thInvoiceData, "invoice")).thenReturn(pdfFile);

    var result = subject.extractInvoice(worker, invoiceForm);

    assertNotNull(result.base64Image());
    assertFalse(result.base64Image().isBlank());
    assertSame(thInvoiceData, result.invoiceData());

    verify(thInvoiceFormMapper).toDomain(invoiceForm);
    verify(invoiceService).extractInvoiceForm(worker, domainInvoiceForm);
    verify(thInvoiceFormMapper).toTh(extractedInvoiceData);
    verify(invoicePDFGenerator).apply(worker, thInvoiceData, "invoice");
  }
}
