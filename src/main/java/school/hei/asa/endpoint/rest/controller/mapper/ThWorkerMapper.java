package school.hei.asa.endpoint.rest.controller.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.endpoint.rest.model.th.ThWorkerLevelHistory;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.WorkerLevelHistory;
import school.hei.asa.repository.DailyExecutionRepository;

@AllArgsConstructor
@Component
public class ThWorkerMapper {

  private final CareProductCodeSupplier careProductCodeSupplier;
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier;
  private final DailyExecutionRepository dailyExecutionRepository;

  public List<ThWorkerLevelHistory> toTh(List<WorkerLevelHistory> histories, Worker worker) {
    ZoneId zoneId = ZoneId.of("UTC");
    List<ThWorkerLevelHistory> result = new ArrayList<>();

    if (histories.isEmpty()) return result;

    for (int i = 0; i < histories.size(); i++) {
      var current = histories.get(i);
      var startDate = current.entranceInstant().atZone(zoneId).toLocalDate();
      var endDate =
          (i == 0)
              ? LocalDate.now()
              : histories.get(i - 1).entranceInstant().atZone(zoneId).toLocalDate();

      var dailyExecutions =
          dailyExecutionRepository.findByWorkerCodeAndDateBetween(
              worker.code(), startDate, endDate);

      double totalDaysWorked = 0;

      for (DailyExecution dailyExecution : dailyExecutions) {
        switch (dailyExecution.type(careProductCodeSupplier.get())) {
          case fullWork -> {
            totalDaysWorked += 1.0;
          }
          case mixedWorkAndCare -> {
            totalDaysWorked +=
                dailyExecution.executions().stream()
                    .filter(me -> !me.mission().isPaidCare(paidCareMissionCodesSupplier.get()))
                    .mapToDouble(MissionExecution::dayPercentage)
                    .sum();
          }
        }
      }

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
