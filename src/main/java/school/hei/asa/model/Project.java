package school.hei.asa.model;

import java.util.Set;

public record Project(String code, String description, Set<Mission> missions) {
  public int maxDurationInDays() {
    return missions.stream().mapToInt(Mission::maxDurationInDays).sum();
  }

  public double executedDays() {
    return missions.stream().mapToDouble(Mission::executedDays).sum();
  }
}
