package school.hei.asa.endpoint.rest.model.th;

import java.time.Month;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThMission {
  String code;
  String title;
  String description;
  List<ThMissionExecution> missionExecutions;
  boolean isCare;
  boolean isUnpaidCare;

  public double executedDays() {
    return missionExecutions.stream().mapToDouble(ThMissionExecution::getDayPercentage).sum();
  }

  public double studentExecutedDays() {
    return missionExecutions.stream()
        .filter(ThMissionExecution::isExecutedByStudent)
        .mapToDouble(ThMissionExecution::getDayPercentage)
        .sum();
  }

  public ThMission filterByWorkerCode(String workerCode) {
    var filteredExecutions =
        missionExecutions.stream().filter(me -> workerCode.equals(me.workerCode)).toList();
    return new ThMission(code, title, description, filteredExecutions, isCare, isUnpaidCare);
  }

  public ThMission filterByMonth(Month month) {
    var filteredExecutions =
        missionExecutions.stream().filter(me -> me.getDate().getMonth() == month).toList();
    return new ThMission(code, title, description, filteredExecutions, isCare, isUnpaidCare);
  }
}
