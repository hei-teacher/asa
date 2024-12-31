package school.hei.asa.model;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
public class Mission {

  private final String code;
  @EqualsAndHashCode.Exclude private final String title;
  @EqualsAndHashCode.Exclude private final String description;
  @EqualsAndHashCode.Exclude private final int maxDurationInDays;
  @EqualsAndHashCode.Exclude private final Product product;

  @EqualsAndHashCode.Exclude private final List<MissionExecution> executions = new ArrayList<>();

  public void add(MissionExecution me) {
    if (!this.equals(me.mission())) {
      throw new IllegalArgumentException(
          String.format("missionExecution.mission=%s is not same as this=%s", me.mission(), this));
    }
    executions.add(me);
  }

  public Mission(
      String code, String title, String description, int maxDurationInDays, Product product) {
    this.code = code;
    this.title = title;
    this.description = description;
    this.maxDurationInDays = maxDurationInDays;
    this.product = product;
    this.product.add(this);
  }

  public double executedDays() {
    return executions.stream().mapToDouble(MissionExecution::dayPercentage).sum();
  }

  public List<Worker> workers() {
    return executions.stream().map(MissionExecution::worker).toList();
  }
}
