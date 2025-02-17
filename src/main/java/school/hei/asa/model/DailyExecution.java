package school.hei.asa.model;

import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;
import static school.hei.asa.model.DailyExecution.Type.mixedWorkAndCare;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record DailyExecution(Worker worker, LocalDate date, List<MissionExecution> executions) {
  public DailyExecution {
    validate(executions);
  }

  private void validate(List<MissionExecution> executions) {
    Set<MissionExecution> executionsAsSet = new HashSet<>(executions);
    if (executionsAsSet.size() < executions.size()) {
      log.warn("duplicate elements in missionExecutions={}", executions);
    }

    var percentagesSum =
        executionsAsSet.stream()
            .mapToDouble(MissionExecution::dayPercentage)
            .map(p -> p * 100)
            .sum();
    if (percentagesSum != 100) {
      throw new IllegalArgumentException(
          "missionPercentages::sum*100 must equal 100, but was: "
              + percentagesSum
              + ", missionExecutions="
              + executions);
    }
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
