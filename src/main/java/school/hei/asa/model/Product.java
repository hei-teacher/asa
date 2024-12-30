package school.hei.asa.model;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
public class Product {

  @Accessors(fluent = true)
  @Getter
  private final String code;

  @EqualsAndHashCode.Exclude
  @Accessors(fluent = true)
  @Getter
  private final String name;

  @EqualsAndHashCode.Exclude
  @Accessors(fluent = true)
  @Getter
  private final String description;

  @EqualsAndHashCode.Exclude private final Set<Mission> missions = new HashSet<>();

  public int maxDurationInDays() {
    return missions.stream().mapToInt(Mission::maxDurationInDays).sum();
  }

  public double executedDays() {
    return missions.stream().mapToDouble(Mission::executedDays).sum();
  }

  /*package-private*/ void add(Mission mission) {
    missions.add(mission);
  }
}
