package school.hei.asa.endpoint.rest.model.th;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class ThContract {
  String level;
  String entranceInstant;
  String endInstant;
  String contractType;
  BigDecimal compensation;
  String company;
  String jobTitle;
  String duration;
  String contractBucketKey;
  String actualWorkedDays;
}
