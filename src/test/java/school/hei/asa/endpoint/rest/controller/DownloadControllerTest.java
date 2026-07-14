package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.time.Duration;
import java.time.YearMonth;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.model.Worker;
import school.hei.asa.service.InvoiceService;

class DownloadControllerTest {

  private final WorkerFromAuthentication workerFromAuthentication =
      mock(WorkerFromAuthentication.class);
  private final WorkerToModelAdder workerToModelAdder = mock(WorkerToModelAdder.class);
  private final BucketComponent bucketComponent = mock(BucketComponent.class);
  private final InvoiceService invoiceService = mock(InvoiceService.class);
  private final DownloadController controller =
      new DownloadController(
          workerFromAuthentication, workerToModelAdder, bucketComponent, invoiceService);

  @Test
  void redirectToPresignedUrlForContractFile_redirects_to_presigned_url() throws Exception {
    var presignedUrl = new URL("http://example.com/presigned-contract");
    when(bucketComponent.presign(eq("contracts/my-file.pdf"), any(Duration.class)))
        .thenReturn(presignedUrl);

    var result = controller.redirectToPresignedUrlForContractFile("my-file.pdf");

    assertEquals("redirect:http://example.com/presigned-contract", result);
  }

  @Test
  void redirectToPresignedUrlForInvoiceFile_redirects_to_presigned_url() throws Exception {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var authentication = mock(Authentication.class);
    var model = new ConcurrentModel();
    var yearMonth = "2025-06";
    var presignedUrl = new URL("http://example.com/presigned-invoice");

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(workerToModelAdder.apply(any(WorkerModelAdderParam.class), eq(model))).thenReturn(worker);
    when(invoiceService.getInvoiceBucketKey(eq(worker), eq(YearMonth.of(2025, 6))))
        .thenReturn("invoice-key.pdf");
    when(bucketComponent.presign(eq("invoices/invoice-key.pdf"), any(Duration.class)))
        .thenReturn(presignedUrl);

    var result = controller.redirectToPresignedUrlForInvoiceFile(model, authentication, yearMonth);

    assertEquals("redirect:http://example.com/presigned-invoice", result);
    assertEquals(2025, model.getAttribute("year"));
  }
}
