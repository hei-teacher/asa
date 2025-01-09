package school.hei.asa.model;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "code")
public class Product {

  private final String code;
  private final String name;
  private final String description;

  private final Set<Mission> missions = new HashSet<>();

  public int maxDurationInDays() {
    return missions.stream().mapToInt(Mission::maxDurationInDays).sum();
  }

  public double executedDays() {
    return missions.stream().mapToDouble(Mission::executedDays).sum();
  }

  public void add(Mission mission) {
    var missionProduct = mission.product();
    if (!this.equals(missionProduct)) {
      throw new IllegalArgumentException(
          String.format("mission.product=%s is not same as this=%s", missionProduct, this));
    }
    missions.add(mission);
  }
}
