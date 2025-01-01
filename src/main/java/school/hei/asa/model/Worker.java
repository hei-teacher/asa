package school.hei.asa.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
  private final String email;

  @ToString.Exclude
  protected final Map<Mission, List<MissionExecution>> executionsByMission = new HashMap<>();

  public final void execute(DailyExecution dailyExecution) {
    var missionExecutions = dailyExecution.executions();
    for (var me : missionExecutions) {
      var mission = me.mission();
      mission.add(me);

      var toPutAsSet = new HashSet<>(executionsByMission.getOrDefault(mission, List.of(me)));
      toPutAsSet.add(me);
      executionsByMission.put(mission, toPutAsSet.stream().toList());
    }
  }
}
