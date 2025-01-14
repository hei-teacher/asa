package school.hei.asa.model;

import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;
import static school.hei.asa.model.DailyExecution.Type.mixedWorkAndCare;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record DailyExecution(Worker worker, LocalDate date, List<MissionExecution> executions) {
  public DailyExecution {
    validate(executions);
  }

  private void validate(List<MissionExecution> executions) {
    var percentagesSum = calculateSumExecutionPercentage(executions);

    if ((int) percentagesSum != 100) {
      throw new IllegalArgumentException(
          "missionPercentages::sum must equal 1, but was: " + percentagesSum);
    }
  }

  private static double calculateSumExecutionPercentage(List<MissionExecution> executions) {
    return executions.stream().mapToDouble(MissionExecution::dayPercentage).map(p -> p * 100).sum();
  }

  public double sumExecutionPercentage() {
    return calculateSumExecutionPercentage(executions);
  }

  public Type type(String careProductCode) {
    var executedMissions =
        executions.stream().map(MissionExecution::mission).collect(Collectors.toSet());
    if (executedMissions.stream().allMatch(mission -> mission.isCare(careProductCode))) {
      return fullCare;
    }
    if (executedMissions.stream().noneMatch(mission -> mission.isCare(careProductCode))) {
      return fullWork;
    }
    return mixedWorkAndCare;
  }

  public enum Type {
    fullWork,
    fullCare,
    mixedWorkAndCare
  }
}
