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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.event.model.SendEmailRequested;
import school.hei.asa.endpoint.rest.model.th.ThInvoice;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.InvoicePDFGenerator;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.model.BankAccount;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.service.event.SendEmailRequestedService;

@SpringBootTest(properties = {"ACCOUNTANTS=test@test.com"})
class InvoiceControllerIT extends FacadeIT {

  @Autowired InvoiceController invoiceController;

  @MockBean WorkerFromAuthentication workerFromAuthentication;
  @MockBean WorkerToModelAdder workerToModelAdder;
  @MockBean BankAccountRepository bankAccountRepository;
  @MockBean BucketComponent bucketComponent;
  @MockBean SendEmailRequestedService sendEmailRequestedService;
  @MockBean InvoicePDFGenerator invoicePDFGenerator;
  Authentication authentication;

  Worker authenticatedWorker;
  Model model;
  BankAccount bankAccount;

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

    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    when(workerToModelAdder.apply(anyString(), any())).thenReturn(authenticatedWorker);
    when(bankAccountRepository.findByWorkerCode("worker-code")).thenReturn(bankAccount);
    when(authentication.getName()).thenReturn("test@test.com");
  }

  @Test
  void can_get_invoice() {
    var invoiceForm =
        new ThInvoiceForm(null, null, null, "", "", "", "", "", false, "", "", "", "", "", "", "");
    String viewName = invoiceController.getInvoicePage(model, authentication, invoiceForm, 2025);

    assertEquals("invoice-generator", viewName);
  }

  @Test
  void can_preview_invoice() {
    setUp();
    var invoiceForm =
        new ThInvoiceForm(null, null, null, "", "", "", "", "", false, "", "", "", "", "", "", "");
    var invoicePreview = invoiceController.previewInvoice(model, authentication, invoiceForm);

    assertEquals(OK, invoicePreview.getStatusCode());
    assertTrue(invoicePreview.hasBody());
  }

  @Test
  void can_send_invoice_when_generated() throws IOException {
    File fakeFile = File.createTempFile("temp", ".pdf");
    Files.write(fakeFile.toPath(), new byte[] {1, 2, 3});

    when(invoicePDFGenerator.apply(any(Worker.class), any(), any())).thenReturn(fakeFile);

    var fakeInvoiceData = mock(ThInvoice.class);
    when(fakeInvoiceData.invoiceData().id()).thenReturn("inv-001");
    when(fakeInvoiceData.invoiceData().yearMonth()).thenReturn("2025-08");

    var fakeInvoice = mock(ThInvoice.class);
    when(fakeInvoice.invoiceData()).thenReturn(fakeInvoiceData.invoiceData());

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

    var response = invoiceController.generateInvoice(model, authentication, invoiceForm);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertTrue(response.getBody().length > 0);

    verify(bucketComponent, times(1)).upload(eq(fakeFile), anyString());
    verify(sendEmailRequestedService, times(1)).accept(any(SendEmailRequested.class));
  }
}
