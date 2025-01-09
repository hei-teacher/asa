package school.hei.asa.endpoint.rest.controller;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.RED;
import static java.time.LocalDate.now;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;
import static school.hei.asa.model.DailyExecution.Type.mixedWorkAndCare;

import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import school.hei.asa.endpoint.rest.model.th.ThYear;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Worker;
import school.hei.asa.service.CalendarService;

@AllArgsConstructor
@Controller
public class CalendarController {

  private final CalendarService calendarService;
  private final WorkerFromAuthentication workerFromAuthentication;

  @GetMapping("/work-and-care-calendar")
  public String getCalendar(Model model, Authentication authentication) {
    var year = now().getYear();
    model.addAttribute("year", year);
    var worker = workerFromAuthentication.apply(authentication).get();
    model.addAttribute(
        "thYear",
        new ThYear(year, "Work & Care days", getColoredDates(year, worker), colorDescription()));
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
