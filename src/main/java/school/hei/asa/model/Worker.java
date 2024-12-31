package school.hei.asa.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
  protected final Map<Mission, List<MissionExecution>> executionsByMission = new HashMap<>();

  public abstract Set<LocalDate> availabilities();

  public final void execute(DailyExecution dailyExecution) {
    var missionExecutions = dailyExecution.executions();
    for (var missionExecution : missionExecutions) {
      var mission = missionExecution.mission();
      mission.addWorker(this);

      var toPutAsSet =
          new HashSet<>(executionsByMission.getOrDefault(mission, List.of(missionExecution)));
      toPutAsSet.add(missionExecution);
      executionsByMission.put(mission, toPutAsSet.stream().toList());
    }
  }

  public final List<MissionExecution> executionsOf(Mission mission) {
    return executionsByMission.get(mission);
  }
}
