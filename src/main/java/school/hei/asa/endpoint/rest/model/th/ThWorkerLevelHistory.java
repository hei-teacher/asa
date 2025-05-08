package school.hei.asa.endpoint.rest.model.th;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class ThWorkerLevelHistory {
  String level;
  Instant entranceInstant;
  String contractType;
  String projectedDaysToWork;
  String actualWorkedDay;
}
