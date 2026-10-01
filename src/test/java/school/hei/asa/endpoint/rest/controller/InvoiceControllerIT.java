package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import school.hei.asa.conf.FacadeITMockedThirdParties;
import school.hei.asa.endpoint.rest.model.th.ThInvoice;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.InvoicePDFGenerator;
import school.hei.asa.endpoint.rest.service.ThInvoiceService;
import school.hei.asa.file.hash.FileHash;
import school.hei.asa.file.hash.FileHashAlgorithm;
import school.hei.asa.model.BankAccount;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.service.InvoiceService;

class InvoiceControllerIT extends FacadeITMockedThirdParties {

  @Autowired InvoiceController invoiceController;

  @MockBean WorkerFromAuthentication workerFromAuthentication;
  @MockBean WorkerToModelAdder workerToModelAdder;
  @MockBean BankAccountRepository bankAccountRepository;
  @MockBean ThInvoiceService thInvoiceService;
  @MockBean InvoicePDFGenerator invoicePDFGenerator;

  Authentication authentication;
  Worker authenticatedWorker;
  Model model;
  BankAccount bankAccount;
  @Autowired private InvoiceService invoiceService;

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    authenticatedWorker =
        new Worker(
            "worker-code",
            "Test Worker",
            "worker@example.com",
            "Full Worker Name",
            "address",
            "random city",
            "nif",
            "stat");
    bankAccount = new BankAccount("", "", "", "", "", authenticatedWorker);
    model = mock(Model.class);
    when(thInvoiceService.generateInvoiceFileName(any(Worker.class)))
        .thenReturn("invoice_2025_08.pdf");
    doNothing().when(thInvoiceService).saveInvoice(any(), any(Worker.class));
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    when(workerToModelAdder.apply(any(WorkerModelAdderParam.class), any()))
        .thenReturn(authenticatedWorker);
    when(bankAccountRepository.findByWorkerCode("worker-code")).thenReturn(bankAccount);
  }

  @Test
  void can_get_invoice() {

    var invoiceForm =
        new ThInvoiceForm(null, null, null, "", "", "", "", "", false, "", "", "", "", "", "", "");
    String viewName = invoiceController.getInvoicePage(model, authentication, invoiceForm, 2025);

    assertEquals("invoice-generator", viewName);
  }

  @Test
  void can_preview_invoice() throws IOException {
    setUp();

    var placeholderForm =
        new ThInvoiceForm(
            "inv-001",
            "2025-08",
            "ref-001",
            "2025-09-03",
            "Invoice for project X",
            "2",
            "500",
            "1000",
            false,
            "Extra desc",
            "1",
            "200",
            "200",
            "1200",
            "1200",
            "FR761234567890");

    var fakeInvoice = new ThInvoice("base64dummy", placeholderForm);

    when(thInvoiceService.extractInvoice(any(Worker.class), any())).thenReturn(fakeInvoice);

    File fakeFile = File.createTempFile("temp", ".pdf");
    Files.write(fakeFile.toPath(), new byte[] {1, 2, 3}); // contenu dummy
    when(invoicePDFGenerator.apply(any(Worker.class), any(), any())).thenReturn(fakeFile);

    var invoiceForm =
        new ThInvoiceForm(null, null, null, "", "", "", "", "", false, "", "", "", "", "", "", "");

    var invoicePreview = invoiceController.previewInvoice(model, authentication, invoiceForm);

    assertEquals(OK, invoicePreview.getStatusCode());
    assertTrue(invoicePreview.hasBody());
  }

  @Test
  void can_send_invoice_when_generated() throws IOException {
    setUp();

    File fakeFile = File.createTempFile("temp", ".pdf");
    Files.write(fakeFile.toPath(), new byte[] {1, 2, 3});

    when(invoicePDFGenerator.apply(any(Worker.class), any(), any())).thenReturn(fakeFile);

    var invoiceForm =
        new ThInvoiceForm(
            "inv-001",
            "2025-08",
            "ref-001",
            "2025-09-03",
            "Invoice for project X",
            "2",
            "500",
            "1000",
            false,
            "Extra desc",
            "1",
            "200",
            "200",
            "1200",
            "1200",
            "FR761234567890");

    var fakeInvoice = new ThInvoice("base64dummy", invoiceForm);
    FileHash fileHash = new FileHash(FileHashAlgorithm.NONE, "/invoices");
    when(thInvoiceService.extractInvoice(any(Worker.class), any())).thenReturn(fakeInvoice);
    var response = invoiceController.generateInvoice(model, authentication, invoiceForm);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertTrue(response.getBody().length > 0);
  }
}
