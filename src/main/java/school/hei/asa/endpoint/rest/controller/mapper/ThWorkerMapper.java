package school.hei.asa.endpoint.rest.controller.mapper;

import static java.time.Instant.now;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.rest.model.th.ThWorkerLevelHistory;
import school.hei.asa.model.Worker;
import school.hei.asa.model.WorkerLevelHistory;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.mapper.MissionMapper;
import school.hei.asa.repository.model.JMissionExecution;

@AllArgsConstructor
@Component
public class ThWorkerMapper {

  public static final String CONTRACT_WITH_TOTAL_WORK_DAYS = "partnerContractor";

  private final MissionExecutionRepository missionExecutionRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final MissionMapper missionMapper;

  public List<ThWorkerLevelHistory> toTh(List<WorkerLevelHistory> histories) {
    ZoneId zoneId = ZoneId.of("UTC");
    List<ThWorkerLevelHistory> result = new ArrayList<>();

    for (int i = 0; i < histories.size(); i++) {
      var current = histories.get(i);
      var nextEntrance = (i == 0) ? now() : histories.get(i - 1).entranceInstant();

      double totalDaysWorked =
          missionExecutionPercentageSumByWorker(
              current.worker(),
              current.entranceInstant().atZone(zoneId).toLocalDate(),
              nextEntrance.atZone(zoneId).toLocalDate());

      var contractType = toWorkerType(current.contractType());
      var totalWorkDays =
          CONTRACT_WITH_TOTAL_WORK_DAYS.equals(current.contractType())
              ? String.valueOf(current.projectedDaysToWork())
              : "-";

      result.add(
          new ThWorkerLevelHistory(
              current.level().getLevel(),
              current.entranceInstant(),
              contractType,
              totalWorkDays,
              String.valueOf(totalDaysWorked)));
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

  private Double missionExecutionPercentageSumByWorker(
      Worker worker, LocalDate startDate, LocalDate endDate) {
    return missionExecutionRepository
        .missionExecutionsByDateBetween(worker, startDate, endDate)
        .stream()
        .filter(
            jme -> {
              var mission = missionMapper.toDomain(jme.getMission());
              boolean isCare = mission.isCare(careProductCodeSupplier.get());
              return !isCare;
            })
        .mapToDouble(JMissionExecution::getDayPercentage)
        .sum();
  }
}
