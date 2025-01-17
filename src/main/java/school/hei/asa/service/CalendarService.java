package school.hei.asa.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.DailyExecutionRepository;

@AllArgsConstructor
@Service
public class CalendarService {

  private final DailyExecutionRepository dailyExecutionRepository;
  private final ProductConf productConf;

  @Transactional
  public Map<DailyExecution.Type, List<LocalDate>> datesByDailyExecutionType(
      Worker worker, int year) {
    Map<DailyExecution.Type, List<LocalDate>> res = new HashMap<>();

    var dailyExecutions = dailyExecutionsBy(worker, year);
    Arrays.stream(DailyExecution.Type.values())
        .forEach(
            dailyExecutionType ->
                res.put(
                    dailyExecutionType,
                    filterByDailyExecutionType(dailyExecutions, dailyExecutionType)));

    return res;
  }

  private List<DailyExecution> dailyExecutionsBy(Worker worker, int year) {
    return dailyExecutionRepository.findAllBy(worker).stream()
        .filter(me -> me.date().getYear() == year)
        .toList();
  }

  private List<LocalDate> filterByDailyExecutionType(
      List<DailyExecution> dayExecutions, DailyExecution.Type dailyExecutionType) {
    return dayExecutions.stream()
        .filter(
            dailyExecution ->
                dailyExecutionType.equals(dailyExecution.type(productConf.careProductCode())))
        .map(DailyExecution::date)
        .toList();
  }
}
