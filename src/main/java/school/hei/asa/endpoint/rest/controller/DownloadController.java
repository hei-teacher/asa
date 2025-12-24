package school.hei.asa.endpoint.rest.controller;

import java.time.Duration;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.service.InvoiceService;

@Controller
@AllArgsConstructor
public class DownloadController {
  private static final String CONTRACTS_FOLDER = "contracts/";
  private static final String INVOICES_FOLDER = "invoices/";
  private final WorkerFromAuthentication workerFromAuthentication;
  private final WorkerToModelAdder workerToModelAdder;
  private final BucketComponent bucketComponent;
  private final InvoiceService invoiceService;

  @GetMapping("/download-contract")
  public String redirectToPresignedUrlForContractFile(@RequestParam String contractBucketKey) {
    String presignedUrl =
        bucketComponent
            .presign(CONTRACTS_FOLDER + contractBucketKey, Duration.ofMinutes(5))
            .toString();
    return "redirect:" + presignedUrl;
  }

  @GetMapping("/download-invoice")
  public String redirectToPresignedUrlForInvoiceFile(
      Model model, Authentication authentication, @RequestParam String yearMonth) {
    var workerCodeOrAuth = workerFromAuthentication.apply(authentication).get().code();
    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    var pattern = DateTimeFormatter.ofPattern("yyyy-MM");
    var date = YearMonth.parse(yearMonth, pattern);
    var invoiceBucketKey = invoiceService.getInvoiceBucketKey(worker, date);

    String presignedUrl =
        bucketComponent
            .presign(INVOICES_FOLDER + invoiceBucketKey, Duration.ofMinutes(5))
            .toString();
    return "redirect:" + presignedUrl;
  }
}
