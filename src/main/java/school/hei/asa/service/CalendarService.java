package school.hei.asa.service;

import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.ZoneId.systemDefault;
import static java.util.Locale.US;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Worker;
import school.hei.asa.model.WorkerCalendar;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.DailyExecutionRepository;

@AllArgsConstructor
@Service
public class CalendarService {

  private final DailyExecutionRepository dailyExecutionRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier;
  private final MissionService missionService;
  private final ContractService contractService;

  @Transactional
  public Map<DailyExecution.Type, List<LocalDate>> datesByDailyExecutionType(
      Worker worker, int year) {
    return new WorkerCalendar(
            worker,
            dailyExecutionRepository.findByWorkerCodeAndDateBetween(
                worker.code(), LocalDate.of(year, JANUARY, 1), LocalDate.of(year, DECEMBER, 31)),
            year,
            new school.hei.asa.model.ProductConf(
                careProductCodeSupplier.get(), paidCareMissionCodesSupplier.get()))
        .datesByDailyExecutionType();
  }

  @Transactional
  public Map<Month, Map<Mission.Type, Double>> missionExecutionPercentageSumByMissionType(
      Worker worker, int year) {
    return new WorkerCalendar(
            worker,
            dailyExecutionRepository.findByWorkerCodeAndDateBetween(
                worker.code(), LocalDate.of(year, JANUARY, 1), LocalDate.of(year, DECEMBER, 31)),
            year,
            new school.hei.asa.model.ProductConf(
                careProductCodeSupplier.get(), paidCareMissionCodesSupplier.get()))
        .missionExecutionPercentageSumByMissionType();
  }

  @Transactional
  public Map<Month, List<LocalDate>> lateReportedDaysByMonth(Worker worker, int year) {
    return new WorkerCalendar(
            worker,
            dailyExecutionRepository.findByWorkerCodeAndDateBetween(
                worker.code(), LocalDate.of(year, JANUARY, 1), LocalDate.of(year, DECEMBER, 31)),
            year,
            new school.hei.asa.model.ProductConf(
                careProductCodeSupplier.get(), paidCareMissionCodesSupplier.get()))
        .lateReportedDaysByMonth();
  }

  public double getRemainingDaysOnActiveContractOrZero(Worker worker) {
    return contractService
        .findActiveContract(worker)
        .map(contract -> getRemainingDaysForContract(worker, contract))
        .orElse(0d);
  }

  public double getRemainingDaysForContract(Worker worker, Contract contract) {
    var startDate = contract.entranceInstant().atZone(systemDefault()).toLocalDate();
    var endDate =
        contract.endInstant() == null
            ? LocalDate.now()
            : contract.endInstant().atZone(systemDefault()).toLocalDate();
    var actualWorkedDays = getActualWorkedDaysByDateByWorker(startDate, worker.code(), endDate);
    var workedDays = actualWorkedDays.equals("-") ? 0d : Double.parseDouble(actualWorkedDays);
    return contract.duration().toDays() - workedDays;
  }

  public String getActualWorkedDaysByDateByWorker(
      LocalDate startDate, String workerCode, LocalDate endDate) {
    var dailyExecutions =
        dailyExecutionRepository.findByWorkerCodeAndDateBetween(workerCode, startDate, endDate);
    return executedDays(dailyExecutions);
  }

  private String executedDays(List<DailyExecution> executions) {
    if (executions.isEmpty()) {
      return "-";
    }
    var result =
        executions.stream()
            .map(
                dailyExecution -> {
                  var type = dailyExecution.type(careProductCodeSupplier.get());
                  if (type.equals(fullWork)) {
                    return 1.0d;
                  } else if (type.equals(fullCare)) {
                    return 0.0d;
                  }
                  return dailyExecution.executions().stream()
                      .map(me -> missionService.isUnpaidCare(me) ? 0.0d : me.dayPercentage())
                      .reduce(0.0d, Double::sum);
                })
            .reduce(0.0d, Double::sum);
    return String.format(US, "%.1f", result);
  }
}
