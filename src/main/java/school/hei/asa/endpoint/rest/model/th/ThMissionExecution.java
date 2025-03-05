package school.hei.asa.endpoint.rest.model.th;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThMissionExecution {
  String missionCode;
  String workerCode;
  LocalDate date;
  double dayPercentage;
  String comment;
  boolean isCare;
  boolean isExecutedByStudent;
}
