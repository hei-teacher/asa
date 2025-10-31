package school.hei.asa.service;

import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.file.bucket.BucketComponent;

@Service
@AllArgsConstructor
public class DownloadService {
  private final BucketComponent bucketComponent;

  public String presign(String bucketKey) {
    if (bucketKey == null || bucketKey.isBlank()) return null;
    return bucketComponent.presign(bucketKey, Duration.ofMinutes(5)).toString();
  }
}
