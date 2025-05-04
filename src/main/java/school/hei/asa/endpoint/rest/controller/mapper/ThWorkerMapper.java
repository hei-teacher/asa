package school.hei.asa.endpoint.rest.controller.mapper;

import static java.time.Instant.now;
import static java.util.stream.IntStream.range;

import java.time.LocalDate;
import java.time.ZoneId;
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

  private final MissionExecutionRepository missionExecutionRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final MissionMapper missionMapper;

  public List<ThWorkerLevelHistory> toTh(List<WorkerLevelHistory> workerLevelHistories) {
    return range(0, workerLevelHistories.size())
        .mapToObj(
            i -> {
              var current = workerLevelHistories.get(i);
              var nextEntrance =
                  (i == 0) ? now() : workerLevelHistories.get(i - 1).entranceInstant();

              var zoneId = ZoneId.of("UTC");

              double totalDaysWorked =
                  missionExecutionPercentageSumByWorker(
                      current.worker(),
                      current.entranceInstant().atZone(zoneId).toLocalDate(),
                      nextEntrance.atZone(zoneId).toLocalDate());

              var contractType =
                  switch (current.contractType()) {
                    case "partnerContractor" -> "Prestation";
                    case "fullTimeEmployee" -> "Salariat";
                    default -> "Alternance";
                  };

              var contractWithTotalWorkDays = "partnerContractor";
              var totalWorkDays =
                  contractWithTotalWorkDays.equals(current.contractType())
                      ? String.valueOf(current.totalWorkDays())
                      : "-";

              return new ThWorkerLevelHistory(
                  current.level().getLevel(),
                  current.entranceInstant(),
                  contractType,
                  totalWorkDays,
                  String.valueOf(totalDaysWorked));
            })
        .toList();
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
