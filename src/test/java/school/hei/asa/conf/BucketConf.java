package school.hei.asa.conf;

import org.springframework.test.context.DynamicPropertyRegistry;
import school.hei.asa.PojaGenerated;

@PojaGenerated
public class BucketConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.s3.bucket", () -> "dummy-bucket");
  }
}
