package school.hei.asa.endpoint.rest.controller.mapper;

import static java.time.Instant.now;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.model.WorkerDayPercentageSummary;

@Slf4j
@AllArgsConstructor
@Component
public class ThWorkerMapper {

  private final MissionExecutionRepository missionExecutionRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;

  public List<ThContract> toTh(List<Contract> contracts) {
    List<ThContract> result = new ArrayList<>();

    for (int i = 0; i < contracts.size(); i++) {
      var current = contracts.get(i);
      var nextEntrance = (i == 0) ? now() : contracts.get(i - 1).entranceInstant();

      double totalDaysWorked =
          missionExecutionPercentageSumByWorker(
              current.worker(), current.entranceInstant(), nextEntrance);

      var contractLevel = current.level();
      var contractType = toWorkerType(contractLevel.type().name());
      var executedDays = current.executions().isEmpty() ? "-" : current.executions().size() + "";

      var compensation =
          switch (contractLevel.type()) {
            case partnerContractor, studentContractor -> contractLevel.dailyPay();
            case fullTimeEmployee -> contractLevel.monthlyPay();
          };

      result.add(
          new ThContract(
              contractLevel.code(),
              current.entranceInstant(),
              contractType,
              executedDays,
              BigDecimal.valueOf(compensation),
              current.jobTitle(),
              current.duration() == null ? "-" : current.duration().toDays() + "",
              current.contractBucketKey()));
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
      Worker worker, Instant startDate, Instant endDate) {
    return missionExecutionRepository.dayPercentageSummary(worker, startDate, endDate).stream()
        .filter(w -> !isCare(w.missionCode()))
        .mapToDouble(WorkerDayPercentageSummary::totalDayPercentage)
        .sum();
  }

  private boolean isCare(String missionCode) {
    return missionCode.startsWith(careProductCodeSupplier.get());
  }
}
