package school.hei.asa.model;

import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
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
                    Collectors.groupingBy(
                            dailyExecution -> dailyExecution.date().getMonth(),
                            Collectors.groupingBy(
                                    this::determineMissionType,
                                    summingInt(dailyExecution -> 1)
                            )
                    )
            );
  }

  private Mission.Type determineMissionType(DailyExecution dailyExecution) {
    var dailyExecutionType = dailyExecution.type(productConf.careProductCode());
    if (DailyExecution.Type.fullWork.equals(dailyExecutionType)) {
      return Mission.Type.work;
    } else if (DailyExecution.Type.fullCare.equals(dailyExecutionType) && hasPaidCare(dailyExecution)) {
      return Mission.Type.paidCare;
    } else if (DailyExecution.Type.mixedWorkAndCare.equals(dailyExecutionType) && hasPaidCare(dailyExecution)) {
      return Mission.Type.paidCare;
    }
    return Mission.Type.unpaidCare;
  }

  private boolean hasPaidCare(DailyExecution dailyExecution) {
    return dailyExecution.executions().stream()
        .anyMatch(
            execution ->
                    Mission.Type.paidCare.equals(execution
                            .mission()
                            .type(productConf.careProductCode(), productConf.paidCareMissionCode())));
  }
}
