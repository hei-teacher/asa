package school.hei.asa.endpoint.rest.controller.mapper;

import static java.time.Instant.now;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.rest.model.th.ThWorkerLevelHistory;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.WorkerLevelHistory;
import school.hei.asa.repository.MissionExecutionRepository;

@AllArgsConstructor
@Component
public class ThWorkerMapper {

  private final MissionExecutionRepository missionExecutionRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;

  public List<ThWorkerLevelHistory> toTh(List<WorkerLevelHistory> histories) {
    ZoneId zoneId = ZoneId.of("UTC");
    List<ThWorkerLevelHistory> result = new ArrayList<>();

    if (histories.isEmpty()) {
      return result;
    }

    LocalDate minDate = histories.stream()
            .map(h -> h.entranceInstant().atZone(zoneId).toLocalDate())
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());

    LocalDate maxDate = LocalDate.now();

    Worker worker = histories.get(0).worker();

    List<MissionExecution> allMissions = missionExecutionRepository
            .missionExecutionsByDateBetween(worker, minDate, maxDate)
            .stream()
            .filter(me -> !isCare(me))
            .toList();

    for (int i = 0; i < histories.size(); i++) {
      var current = histories.get(i);
      var nextEntrance = (i == 0) ? Instant.now() : histories.get(i - 1).entranceInstant();

      LocalDate start = current.entranceInstant().atZone(zoneId).toLocalDate();
      LocalDate end = nextEntrance.atZone(zoneId).toLocalDate();

      double totalDaysWorked = allMissions.stream()
              .filter(me -> !me.date().isBefore(start) && !me.date().isAfter(end))
              .mapToDouble(MissionExecution::dayPercentage)
              .sum();

      var contractType = toWorkerType(current.contractType());
      var totalWorkDays = Objects.toString(current.projectedDaysToWork(), "-");

      result.add(
              new ThWorkerLevelHistory(
                      current.level().getLevel(),
                      current.entranceInstant(),
                      contractType,
                      totalWorkDays,
                      String.valueOf(totalDaysWorked),
                      current.salary(),
                      current.jobTitle(),
                      current.contractDuration()));
    }

    return result;
  }

  public String toWorkerType(String contractType) {
    return switch (contractType) {
      case "partnerContractor" -> "Prestataire";
      case "fullTimeEmployee" -> "Salarié";
      case null -> "";
      default -> "Alternant";
    };
  }

  private boolean isCare(MissionExecution me) {
    var mission = me.mission();
    return mission.isCare(careProductCodeSupplier.get());
  }
}
