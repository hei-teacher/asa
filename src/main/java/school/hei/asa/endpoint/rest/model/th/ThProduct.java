package school.hei.asa.endpoint.rest.model.th;

import java.time.Month;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class ThProduct {
  String code;
  String name;
  String description;
  List<ThMission> missions;
  boolean isCare;

  public double executedDays() {
    return missions.stream().mapToDouble(ThMission::executedDays).sum();
  }

  public ThProduct filterByWorkerCode(String workerCode) {
    var filteredMissions = missions.stream().map(m -> m.filterByWorkerCode(workerCode)).toList();
    return new ThProduct(code, name, description, filteredMissions, isCare);
  }

  public ThProduct filterByMonth(Month month) {
    var filteredMissions = missions.stream().map(m -> m.filterByMonth(month)).toList();
    return new ThProduct(code, name, description, filteredMissions, isCare);
  }
}
