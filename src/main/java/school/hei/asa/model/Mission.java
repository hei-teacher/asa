package school.hei.asa.model;

import static java.util.stream.Collectors.toSet;
import static school.hei.asa.model.Mission.Type.paidCare;
import static school.hei.asa.model.Mission.Type.unpaidCare;
import static school.hei.asa.model.Mission.Type.work;

import java.util.HashSet;
import java.util.List;
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

  public Type type(String careProductCode, List<String> paidCareProductCodes) {
    var isNotCare = !isCare(careProductCode);
    if (isNotCare) {
      return work;
    } else {
      if (isPaidCare(paidCareProductCodes)) {
        return paidCare;
      }
      return unpaidCare;
    }
  }

  public enum Type {
    work,
    paidCare,
    unpaidCare
  }

  public double executedDays() {
    return executions.stream().mapToDouble(MissionExecution::dayPercentage).sum();
  }

  public Set<Worker> workers() {
    return executions.stream().map(MissionExecution::worker).collect(toSet());
  }

  public boolean isCare(String careProductCode) {
    return product().isCare(careProductCode);
  }

  public boolean isPaidCare(List<String> paidCareMissionCodes) {
    return paidCareMissionCodes.contains(code);
  }
}
