package school.hei.asa.endpoint.rest.controller.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.endpoint.rest.model.th.ThWorkerLevelHistory;
import school.hei.asa.model.WorkerLevelHistory;

import java.util.List;

@AllArgsConstructor
@Component
public class ThWorkerMapper {

  public ThWorkerLevelHistory toTh(WorkerLevelHistory workerLevelHistory) {
    var contractType = switch (workerLevelHistory.contractType()) {
      case "partnerContractor" -> "Prestation";
      case "fullTimeEmployee" -> "Salariat";
      default -> "Alternance";
    };
    var contractWithTotalWorkDays = "partnerContractor";
    var totalWorkDays = contractWithTotalWorkDays.equals(workerLevelHistory.contractType())
            ? String.valueOf(workerLevelHistory.totalWorkDays())
            : "-";

    return new ThWorkerLevelHistory(
        workerLevelHistory.level().getLevel(),
            workerLevelHistory.entranceInstant(),
            contractType,
            totalWorkDays
            );
  }

  public List<ThWorkerLevelHistory> toTh(List<WorkerLevelHistory> workerLevelHistories) {
    return workerLevelHistories.stream().map(this::toTh).toList();
  }
}
