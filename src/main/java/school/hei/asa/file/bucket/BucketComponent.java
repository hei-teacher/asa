package school.hei.asa.file.bucket;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import school.hei.asa.file.hash.FileHash;

@Profile("prod")
@Component
@AllArgsConstructor
public class BucketComponent implements BucketPort {
  private final BucketConf bucketConf;

  @Override
  public FileHash upload(java.io.File file, String bucketKey) {
    return null;
  }

  @Override
  public java.io.File download(String bucketKey) {
    return null;
  }

  @Override
  public java.net.URL presign(String bucketKey, java.time.Duration expiration) {
    return null;
  }

  @Override
  public String getBucketName() {
    return bucketConf.getBucketName();
  }
}