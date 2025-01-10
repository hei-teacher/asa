package school.hei.asa.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "code")
public abstract sealed class Worker permits Contractor, FullTimeEmployee {
  private final String code;
  private final String name;
  private final String email;

  @ToString.Exclude
  protected final Map<Mission, Set<MissionExecution>> executionsByMission = new HashMap<>();

  public final void execute(DailyExecution dailyExecution) {
    var missionExecutions = dailyExecution.executions();
    for (var me : missionExecutions) {
      var mission = me.mission();
      mission.add(me);

      var toPut = new HashSet<>(executionsByMission.getOrDefault(mission, Set.of(me)));
      toPut.add(me);
      executionsByMission.put(mission, toPut);
    }
  }
}
