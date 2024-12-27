package school.hei.asa.model;

import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Mission {

  private final String code;
  @EqualsAndHashCode.Exclude private final String title;
  @EqualsAndHashCode.Exclude private final String description;
  @EqualsAndHashCode.Exclude private final int maxDurationInDays;

  @EqualsAndHashCode.Exclude private final Set<Worker> workers = new HashSet<>();

  /*package-private*/ void addWorker(Worker worker) {
    workers.add(worker);
  }

  public Mission(String code, String title, String description, int maxDurationInDays) {
    this.code = code;
    this.title = title;
    this.description = description;
    this.maxDurationInDays = maxDurationInDays;
  }

  public double executedDays() {
    return workers.stream()
        .flatMap(worker -> worker.executionsOf(this).values().stream())
        .mapToDouble(Double::doubleValue)
        .sum();
  }
}
