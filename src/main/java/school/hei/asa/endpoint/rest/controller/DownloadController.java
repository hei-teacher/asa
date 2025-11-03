package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.service.DownloadService;

@Controller
@AllArgsConstructor
public class DownloadController {

  private final DownloadService downloadService;

  @GetMapping("/download-contract")
  public String redirectToPresignedUrl(@RequestParam String contractBucketKey) {
    String presignedUrl = downloadService.presign(contractBucketKey);
    return "redirect:" + presignedUrl;
  }
}
