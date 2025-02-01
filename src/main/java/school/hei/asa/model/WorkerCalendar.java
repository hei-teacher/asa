package school.hei.asa.model;

import static java.util.stream.Collectors.*;
import static school.hei.asa.model.DailyExecution.Type.*;
import static school.hei.asa.model.Mission.Type.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
public class WorkerCalendar {

  private final Worker worker;
  private final int year;
  private final ProductConf productConf;

  private final List<DailyExecution> dailyExecutions;

  public WorkerCalendar(Worker worker, int year, ProductConf productConf) {
    this.worker = worker;
    this.year = year;
    this.productConf = productConf;
    this.dailyExecutions =
        worker.dailyExecutions().stream()
            .filter(me -> year == me.date().getYear())
            .collect(toList());
  }

  public Map<DailyExecution.Type, List<LocalDate>> datesByDailyExecutionType() {
    Map<DailyExecution.Type, List<LocalDate>> res = new HashMap<>();

    Arrays.stream(DailyExecution.Type.values())
        .forEach(
            dailyExecutionType ->
                res.put(
                    dailyExecutionType,
                    filterByDailyExecutionType(dailyExecutions, dailyExecutionType)));

    return res;
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

  public Map<Month, Map<Mission.Type, Integer>> countMissionTypeByMonth() {
    return dailyExecutions.stream()
        .collect(
            groupingBy(
                dailyExecution -> dailyExecution.date().getMonth(),
                groupingBy(this::determineMissionType, summingInt(dailyExecution -> 1))));
  }

  private Mission.Type determineMissionType(DailyExecution dailyExecution) {
    var dailyExecutionType = dailyExecution.type(productConf.careProductCode());
    if (fullWork.equals(dailyExecutionType)) {
      return work;
    } else if (fullCare.equals(dailyExecutionType) && hasPaidCare(dailyExecution)) {
      return paidCare;
    } else if (mixedWorkAndCare.equals(dailyExecutionType) && hasPaidCare(dailyExecution)) {
      return paidCare;
    }
    return unpaidCare;
  }

  private boolean hasPaidCare(DailyExecution dailyExecution) {
    return dailyExecution.executions().stream()
        .anyMatch(
            execution ->
                paidCare.equals(
                    execution
                        .mission()
                        .type(productConf.careProductCode(), productConf.paidCareMissionCode())));
  }
}
