package school.hei.asa.model;

import java.time.LocalDate;
import java.util.Map;

public record DayExecution(Worker worker, LocalDate date, Map<Mission, Double> missionPercentages) {
  public DayExecution {
    validate(missionPercentages);
  }

  private void validate(Map<Mission, Double> missionPercentages) {
    var percentagesSum =
        missionPercentages.values().stream().mapToDouble(Double::doubleValue).sum();
    if (percentagesSum != 1) {
      throw new IllegalArgumentException("missionPercentages.sum() must equal 1");
    }
  }
}
