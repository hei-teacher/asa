package school.hei.asa.endpoint.rest.controller;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.RED;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toMap;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;
import static school.hei.asa.model.DailyExecution.Type.mixedWorkAndCare;

import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.endpoint.rest.model.th.ThYear;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Worker;
import school.hei.asa.service.CalendarService;
import school.hei.asa.service.ContractService;

@Controller
public class CalendarController {

  private final CalendarService calendarService;
  private final WorkerFromAuthentication workerFromAuthentication;
  private final WorkerToModelAdder workerToModelAdder;
  private final ContractService contractService;
  private final int lowRemainingDaysThreshold;

  public CalendarController(
      CalendarService calendarService,
      WorkerFromAuthentication workerFromAuthentication,
      WorkerToModelAdder workerToModelAdder,
      ContractService contractService,
      @Value("${asa.low.contract.days.threshold}") int lowRemainingDaysThreshold) {
    this.calendarService = calendarService;
    this.workerFromAuthentication = workerFromAuthentication;
    this.workerToModelAdder = workerToModelAdder;
    this.contractService = contractService;
    this.lowRemainingDaysThreshold = lowRemainingDaysThreshold;
  }

  @GetMapping("/work-and-care-calendar")
  public String getCalendar(
      Model model,
      Authentication authentication,
      @RequestParam(required = false) String workerCode,
      @RequestParam(required = false) Integer year) {
    year = year == null ? now().getYear() : year;
    model.addAttribute("year", year);

    var workerCodeOrAuth =
        workerCode == null || workerCode.isBlank()
            ? workerFromAuthentication.apply(authentication).get().code()
            : workerCode;

    var worker =
        workerToModelAdder.apply(
            new WorkerModelAdderParam(
                workerCode, workerFromAuthentication.apply(authentication).get().code()),
            model);

    var missionTypeByMonth =
        calendarService.missionExecutionPercentageSumByMissionType(worker, year);
    Map<Month, Map<Mission.Type, Double>> missionCounts = new HashMap<>();
    missionTypeByMonth.forEach(
        (month, counts) -> {
          Map<Mission.Type, Double> typeCounts =
              counts.entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
          missionCounts.put(month, typeCounts);
        });
    var lateReportedDaysByMonth = calendarService.lateReportedDaysByMonth(worker, year);

    Double remainingDays = contractService.getRemainingDaysByWorker(worker);

    boolean showWarning = remainingDays != null && remainingDays < lowRemainingDaysThreshold;

    model.addAttribute("remainingDays", remainingDays);
    model.addAttribute("showWarning", showWarning);

    model.addAttribute("workerCode", workerCodeOrAuth);
    model.addAttribute("currentYear", now().getYear());
    model.addAttribute(
        "thYear",
        new ThYear(
            year,
            "Work & Care days - " + worker.name(),
            getColoredDates(year, worker),
            colorDescription(),
            missionCounts,
            lateReportedDaysByMonth));

    return "calendar";
  }

  private static Map<Color, String> colorDescription() {
    return Map.of(
        BLUE, "Today",
        GREEN, "Fully executed work day that has no care mission",
        RED, "Days that fully have care missions, including vacation and team building events",
        MAGENTA, "Days that have a mix of work and care missions");
  }

  private Map<LocalDate, Color> getColoredDates(int year, Worker worker) {
    Map<LocalDate, Color> coloredDays = new HashMap<>();
    coloredDays.put(now(), BLUE); // put it first so that today is re-colored if fully executed
    var datesByDailyExecutionType = calendarService.datesByDailyExecutionType(worker, year);
    datesByDailyExecutionType.get(fullWork).forEach(date -> coloredDays.put(date, GREEN));
    datesByDailyExecutionType.get(fullCare).forEach(date -> coloredDays.put(date, RED));
    datesByDailyExecutionType.get(mixedWorkAndCare).forEach(date -> coloredDays.put(date, MAGENTA));
    return coloredDays;
  }
}
