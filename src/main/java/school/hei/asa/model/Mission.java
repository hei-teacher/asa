package school.hei.asa.model;

import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@EqualsAndHashCode(of = "code")
public class Mission {

  private final String code;
  private final String title;
  private final String description;
  private final int maxDurationInDays;
  private final Product product;

  private final Set<MissionExecution> executions = new HashSet<>();

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

  public Set<Worker> workers() {
    return executions.stream().map(MissionExecution::worker).collect(toSet());
  }

  public boolean isCare(String careProductCode) {
    return careProductCode.equals(product.code());
  }
}
