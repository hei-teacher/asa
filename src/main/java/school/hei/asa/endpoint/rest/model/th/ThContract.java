package school.hei.asa.endpoint.rest.model.th;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class ThContract {
  String level;
  Instant entranceInstant;
  String contractType;
  String actualWorkedDay;
  BigDecimal compensation;
  String jobTitle;
  String duration;
  String contractBucketKey;
}
