package school.hei.asa.service;

import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Worker;
import school.hei.asa.model.WorkerCalendar;
import school.hei.asa.repository.DailyExecutionRepository;

@AllArgsConstructor
@Service
public class CalendarService {

  private final DailyExecutionRepository dailyExecutionRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier;
  private final Mailer mailer;
  private final ContractAlertService contractAlertService;
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

  public Optional<String> contractAlertMessage(Worker worker, int year) {
    var calendar =
        new WorkerCalendar(
            worker,
            List.of(),
            year,
            new school.hei.asa.model.ProductConf(
                careProductCodeSupplier.get(), paidCareMissionCodesSupplier.get()));
    var remaining = contractService.getRemainingDaysByWorker(worker);
    var exhaustionError = calendar.contractAlertMessage(remaining);
    if (exhaustionError.isPresent()) {
      return exhaustionError;
    }
    return contractAlertService.contractAlertMessage(worker);
  }
}
