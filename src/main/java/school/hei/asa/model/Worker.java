package school.hei.asa.model;

import static java.util.stream.Collectors.toSet;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
@ToString
public abstract sealed class Worker permits Contractor, FullTimeEmployee {
  public final UUID id;
  public final String name;
  @ToString.Exclude protected final Map<Mission, Set<LocalDate>> datedMissions = new HashMap<>();

  public abstract Set<LocalDate> availabilities();

  public final void execute(Mission mission, Set<LocalDate> dates) {
    mission.addWorker(this);

    var toStoreInMap = new HashSet<>(datedMissions.getOrDefault(mission, dates));
    toStoreInMap.addAll(dates);
    datedMissions.put(mission, toStoreInMap);
  }

  public final Set<MissionExecution> missionExecutions() {
    return datedMissions.entrySet().stream()
        .map(entry -> new MissionExecution(entry.getKey(), this, entry.getValue()))
        .collect(toSet());
  }
}
