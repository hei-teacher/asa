package school.hei.asa.endpoint.rest.controller.mapper;

import static java.time.Instant.now;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
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
    if (histories.isEmpty()) {
      return List.of();
    }

    ZoneId zoneId = ZoneId.of("UTC");
    Worker worker = histories.get(0).worker();

    LocalDate minDate = histories.stream()
            .map(h -> h.entranceInstant().atZone(zoneId).toLocalDate())
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());

    LocalDate maxDate = LocalDate.now();

    Map<LocalDate, Double> missionByDate = missionExecutionRepository
            .missionExecutionsByDateBetween(worker, minDate, maxDate)
            .stream()
            .filter(me -> !isCare(me))
            .collect(Collectors.groupingBy(
                    MissionExecution::date,
                    Collectors.summingDouble(MissionExecution::dayPercentage)
            ));

    List<ThWorkerLevelHistory> result = new ArrayList<>();

    for (int i = 0; i < histories.size(); i++) {
      var current = histories.get(i);
      var nextEntrance = (i == 0) ? now() : histories.get(i - 1).entranceInstant();

      LocalDate start = current.entranceInstant().atZone(zoneId).toLocalDate();
      LocalDate end = nextEntrance.atZone(zoneId).toLocalDate();

      double totalDaysWorked = missionByDate.entrySet().stream()
              .filter(e -> !e.getKey().isBefore(start) && !e.getKey().isAfter(end))
              .mapToDouble(Map.Entry::getValue)
              .sum();

      result.add(new ThWorkerLevelHistory(
              current.level().getLevel(),
              current.entranceInstant(),
              toWorkerType(current.contractType()),
              Objects.toString(current.projectedDaysToWork(), "-"),
              String.valueOf(totalDaysWorked),
              current.salary(),
              current.jobTitle(),
              current.contractDuration()
      ));
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
    return me.mission().isCare(careProductCodeSupplier.get());
  }
}
