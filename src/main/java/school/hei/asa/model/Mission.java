package school.hei.asa.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Mission {

  public final UUID id;
  @EqualsAndHashCode.Exclude private final String title;
  @EqualsAndHashCode.Exclude private final String description;
  @EqualsAndHashCode.Exclude private final int maxDurationInDays;

  @EqualsAndHashCode.Exclude private final Set<Worker> workers = new HashSet<>();

  /*package-private*/ void addWorker(Worker worker) {
    workers.add(worker);
  }

  public Mission(UUID id, String title, String description, int maxDurationInDays) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.maxDurationInDays = maxDurationInDays;
  }

  public int executedDays() {
    return workers.stream()
        .flatMap((Worker worker) -> worker.missionExecutions().stream())
        .filter(me -> this.equals(me.mission()))
        .mapToInt(me -> me.dates().size())
        .sum();
  }
}
