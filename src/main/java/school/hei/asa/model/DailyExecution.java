package school.hei.asa.model;

import java.time.LocalDate;
import java.util.List;

public record DailyExecution(Worker worker, LocalDate date, List<MissionExecution> executions) {
  public DailyExecution {
    validate(executions);
  }

  private void validate(List<MissionExecution> executions) {
    var percentagesSum = executions.stream().mapToDouble(MissionExecution::dayPercentage).sum();
    if (percentagesSum != 1) {
      throw new IllegalArgumentException(
          "missionPercentages::sum must equal 1, but was: " + percentagesSum);
    }
  }
}
