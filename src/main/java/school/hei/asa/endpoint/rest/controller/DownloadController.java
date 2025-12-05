package school.hei.asa.endpoint.rest.controller;

import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.file.bucket.BucketComponent;

@Controller
@AllArgsConstructor
public class DownloadController {
  private static final String CONTRACTS_FOLDER = "contracts/";
  private static final String INVOICES_FOLDER = "invoices/";
  private final BucketComponent bucketComponent;

  @GetMapping("/download-contract")
  public String redirectToPresignedUrlForContractFile(@RequestParam String contractBucketKey) {
    String presignedUrl =
        bucketComponent
            .presign(CONTRACTS_FOLDER + contractBucketKey, Duration.ofMinutes(5))
            .toString();
    return "redirect:" + presignedUrl;
  }

  @GetMapping("/download-invoice")
  public String redirectToPresignedUrlForInvoiceFile(@RequestParam String invoiceBucketKey) {
    String presignedUrl =
        bucketComponent
            .presign(INVOICES_FOLDER + invoiceBucketKey, Duration.ofMinutes(5))
            .toString();
    return "redirect:" + presignedUrl;
  }
}
