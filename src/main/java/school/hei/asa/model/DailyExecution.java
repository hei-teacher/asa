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
    var percentagesSum = executions.stream().mapToDouble(MissionExecution::dayPercentage).sum();
    if (percentagesSum != 1) {
      throw new IllegalArgumentException(
          "missionPercentages::sum must equal 1, but was: " + percentagesSum);
    }
  }

  public Type type(String careProductCode) {
    var productCodesOfExecutions =
        executions.stream().map(me -> me.mission().product().code()).collect(Collectors.toSet());
    if (productCodesOfExecutions.stream().allMatch(careProductCode::equals)) {
      return fullCare;
    }
    if (productCodesOfExecutions.stream().noneMatch(careProductCode::equals)) {
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
