package school.hei.asa.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
@ToString
public abstract sealed class Worker permits Contractor, FullTimeEmployee {
  private final String code;
  private final String name;

  @ToString.Exclude
  protected final Map<Mission, Map<LocalDate, Double>> executionsByMission = new HashMap<>();

  public abstract Set<LocalDate> availabilities();

  public final void execute(DailyMissionExecution mde) {
    var missionPercentages = mde.missionPercentages();
    for (var mission : missionPercentages.keySet()) {
      mission.addWorker(this);

      var date = mde.date();
      var missionPercentage = missionPercentages.get(mission);
      var toPut =
          new HashMap<>(executionsByMission.getOrDefault(mission, Map.of(date, missionPercentage)));
      toPut.put(date, missionPercentage);
      executionsByMission.put(mission, toPut);
    }
  }

  public final Map<LocalDate, Double> executionsOf(Mission mission) {
    return executionsByMission.get(mission);
  }
}
