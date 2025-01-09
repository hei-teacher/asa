package school.hei.asa.model;

import java.util.ArrayList;
import java.util.List;
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

  private final List<MissionExecution> executions = new ArrayList<>();

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

  public boolean isCare(String careProductCode) {
    return careProductCode.equals(product.code());
  }
}
