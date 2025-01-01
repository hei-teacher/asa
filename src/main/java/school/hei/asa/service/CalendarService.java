package school.hei.asa.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionExecutionRepository;

@Service
public class CalendarService {

  private final String careProductCode;
  private final MissionExecutionRepository missionExecutionRepository;

  public CalendarService(
      @Value("${asa.care.product.code}") String careProductCode,
      MissionExecutionRepository missionExecutionRepository) {
    this.careProductCode = careProductCode;
    this.missionExecutionRepository = missionExecutionRepository;
  }

  @Transactional
  public Map<DailyExecution.Type, List<LocalDate>> datesByDailyExecutionType(
      Worker worker, int year) {
    Map<DailyExecution.Type, List<LocalDate>> res = new HashMap<>();

    var dailyExecutions =
        missionExecutionRepository.findAllDailyExecutionBy(worker).stream()
            .filter(me -> me.date().getYear() == year)
            .toList();
    Arrays.stream(DailyExecution.Type.values())
        .forEach(
            dailyExecutionType ->
                res.put(
                    dailyExecutionType,
                    filterByDailyExecutionType(dailyExecutions, dailyExecutionType)));

    return res;
  }

  private List<LocalDate> filterByDailyExecutionType(
      List<DailyExecution> dayExecutionsOfTheYear, DailyExecution.Type dailyExecutionType) {
    return dayExecutionsOfTheYear.stream()
        .filter(dailyExecution -> dailyExecutionType.equals(dailyExecution.type(careProductCode)))
        .map(DailyExecution::date)
        .toList();
  }
}
