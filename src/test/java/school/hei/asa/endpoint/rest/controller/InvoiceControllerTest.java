package school.hei.asa.endpoint.rest.controller;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Files;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import school.hei.asa.endpoint.rest.model.th.ThInvoice;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.endpoint.rest.model.th.ThMonthInvoiceStatus;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.InvoicePDFGenerator;
import school.hei.asa.endpoint.rest.service.ThInvoiceService;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.Worker;
import school.hei.asa.service.InvoiceService;

class InvoiceControllerTest {

  private final WorkerFromAuthentication workerFromAuthentication =
      mock(WorkerFromAuthentication.class);
  private final WorkerToModelAdder workerToModelAdder = mock(WorkerToModelAdder.class);
  private final InvoicePDFGenerator invoicePDFGenerator = mock(InvoicePDFGenerator.class);
  private final InvoiceService invoiceService = mock(InvoiceService.class);
  private final BucketComponent bucketComponent = mock(BucketComponent.class);
  private final ThInvoiceService thInvoiceService = mock(ThInvoiceService.class);
  private final InvoiceController subject =
      new InvoiceController(
          workerFromAuthentication,
          workerToModelAdder,
          invoicePDFGenerator,
          invoiceService,
          bucketComponent,
          thInvoiceService);

  private final Worker worker =
      new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
  private final Authentication authentication = mock(Authentication.class);
  private final ThInvoiceForm thInvoiceForm =
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
  private final YearMonth expectedYearMonth = YearMonth.of(2025, 1);

  @BeforeEach
  void setUp() {
    reset(
        workerFromAuthentication,
        workerToModelAdder,
        invoicePDFGenerator,
        invoiceService,
        bucketComponent,
        thInvoiceService);
  }

  @Test
  void getInvoicePage_with_null_year_uses_current_year() {
    var currentYear = now().getYear();
    var monthInvoiceStatuses = List.of(new ThMonthInvoiceStatus(expectedYearMonth, true));
    var invoiceReference = Optional.of(new InvoiceReference("inv-1", expectedYearMonth, 1, worker));

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(workerToModelAdder.apply(new WorkerModelAdderParam(null, "W-001"), any(Model.class)))
        .thenReturn(worker);
    when(thInvoiceService.getMonthInvoiceStatusForWorker(worker, currentYear))
        .thenReturn(monthInvoiceStatuses);
    when(invoiceService.findInvoiceReference(worker, expectedYearMonth))
        .thenReturn(invoiceReference);

    Model model = new ConcurrentModel();
    String viewName = subject.getInvoicePage(model, authentication, thInvoiceForm, null);

    assertEquals("invoice-generator", viewName);
    assertEquals(currentYear, model.getAttribute("year"));
    assertEquals(currentYear, model.getAttribute("currentYear"));
    assertEquals("2025-01", model.getAttribute("yearMonthReference"));
    assertSame(invoiceReference.get(), model.getAttribute("invoiceReference"));
    assertSame(monthInvoiceStatuses, model.getAttribute("monthInvoiceStatuses"));
    verify(workerFromAuthentication).apply(authentication);
    verify(workerToModelAdder).apply(new WorkerModelAdderParam(null, "W-001"), model);
    verify(thInvoiceService).getMonthInvoiceStatusForWorker(worker, currentYear);
    verify(invoiceService).findInvoiceReference(worker, expectedYearMonth);
  }

  @Test
  void getInvoicePage_with_specific_year() {
    var year = 2024;
    var monthInvoiceStatuses = List.of(new ThMonthInvoiceStatus(expectedYearMonth, false));
    var invoiceReference = Optional.of(new InvoiceReference("inv-1", expectedYearMonth, 1, worker));

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(workerToModelAdder.apply(new WorkerModelAdderParam(null, "W-001"), any(Model.class)))
        .thenReturn(worker);
    when(thInvoiceService.getMonthInvoiceStatusForWorker(worker, year))
        .thenReturn(monthInvoiceStatuses);
    when(invoiceService.findInvoiceReference(worker, expectedYearMonth))
        .thenReturn(invoiceReference);

    Model model = new ConcurrentModel();
    String viewName = subject.getInvoicePage(model, authentication, thInvoiceForm, year);

    assertEquals("invoice-generator", viewName);
    assertEquals(year, model.getAttribute("year"));
    assertEquals("2025-01", model.getAttribute("yearMonthReference"));
    assertSame(invoiceReference.get(), model.getAttribute("invoiceReference"));
    assertSame(monthInvoiceStatuses, model.getAttribute("monthInvoiceStatuses"));
    verify(thInvoiceService).getMonthInvoiceStatusForWorker(worker, year);
    verify(invoiceService).findInvoiceReference(worker, expectedYearMonth);
  }

  @Test
  void previewInvoice_happy_path(@TempDir File tempDir) {
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
    var thInvoice = new ThInvoice("base64data", thInvoiceData);
    var pdfFile = new File(tempDir, "preview.pdf");

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(workerToModelAdder.apply(new WorkerModelAdderParam(null, "W-001"), any(Model.class)))
        .thenReturn(worker);
    when(thInvoiceService.extractInvoice(worker, thInvoiceForm)).thenReturn(thInvoice);
    when(invoicePDFGenerator.apply(worker, thInvoiceData, "invoice")).thenReturn(pdfFile);

    Model model = new ConcurrentModel();
    ResponseEntity<Resource> response =
        subject.previewInvoice(model, authentication, thInvoiceForm);

    assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
    assertTrue(
        response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).getFirst().contains("inline"));
    assertInstanceOf(FileSystemResource.class, response.getBody());
    assertEquals(pdfFile, ((FileSystemResource) response.getBody()).getFile());
    verify(thInvoiceService).extractInvoice(worker, thInvoiceForm);
    verify(invoicePDFGenerator).apply(worker, thInvoiceData, "invoice");
  }

  @Test
  void generateInvoice_happy_path(@TempDir File tempDir) throws Exception {
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
    var thInvoice = new ThInvoice("base64data", thInvoiceData);
    var pdfFile = new File(tempDir, "generated.pdf");
    Files.writeString(pdfFile.toPath(), "dummy pdf content");
    var fileName = "FAC-NUM-2025-W-001-1.pdf";

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(workerToModelAdder.apply(new WorkerModelAdderParam(null, "W-001"), any(Model.class)))
        .thenReturn(worker);
    when(thInvoiceService.extractInvoice(worker, thInvoiceForm)).thenReturn(thInvoice);
    when(invoicePDFGenerator.apply(worker, thInvoiceData, "invoice")).thenReturn(pdfFile);
    when(thInvoiceService.generateInvoiceFileName(worker)).thenReturn(fileName);

    Model model = new ConcurrentModel();
    ResponseEntity<byte[]> response = subject.generateInvoice(model, authentication, thInvoiceForm);

    assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
    assertTrue(
        response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).getFirst().contains(fileName));
    assertArrayEquals(Files.readAllBytes(pdfFile.toPath()), response.getBody());
    verify(thInvoiceService).saveInvoiceReference(thInvoiceData, worker);
    verify(thInvoiceService).generateInvoiceFileName(worker);
    verify(bucketComponent).upload(pdfFile, "invoices/" + fileName);
    verify(invoiceService).sendGenerateInvoiceEvent("th-id");
  }
}
