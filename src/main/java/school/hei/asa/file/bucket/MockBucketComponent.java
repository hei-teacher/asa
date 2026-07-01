package school.hei.asa.file.bucket;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import school.hei.asa.file.hash.FileHash;
import school.hei.asa.file.hash.FileHashAlgorithm;

@Profile("mock")
@Component
public class MockBucketComponent implements BucketComponent {

  @Override
  public FileHash upload(File file, String bucketKey) {
    return new FileHash(FileHashAlgorithm.NONE, null);
  }

  @Override
  public File download(String bucketKey) {
    return new File(bucketKey);
  }

  @Override
  @SneakyThrows
  public URL presign(String bucketKey, Duration expiration) {
    return new URL("http://localhost/" + bucketKey);
  }

  @Override
  public String getBucketName() {
    return "mock-bucket";
  }
}
