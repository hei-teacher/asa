package school.hei.asa.endpoint.rest.model.th;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.Instant;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class ThWorkerLevelHistory {
  String level;
  Instant entranceInstant;
  String contractType;
  String totalWorkDays;
  String totalDaysWorked;
}
