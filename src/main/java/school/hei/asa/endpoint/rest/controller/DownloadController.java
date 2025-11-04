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

  private final BucketComponent bucketComponent;

  @GetMapping("/download-contract")
  public String redirectToPresignedUrl(@RequestParam String contractBucketKey) {
    String presignedUrl =
        bucketComponent.presign(contractBucketKey, Duration.ofMinutes(5)).toString();
    return "redirect:" + presignedUrl;
  }
}
