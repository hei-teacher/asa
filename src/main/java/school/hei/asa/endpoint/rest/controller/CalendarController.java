package school.hei.asa.endpoint.rest.controller;

import static java.awt.Color.RED;
import static java.time.LocalDate.now;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import school.hei.asa.endpoint.rest.model.th.ThYear;

@Controller
public class CalendarController {

  @GetMapping("/calendar")
  public String getCalendar(Model model) {
    var year = 2025;
    model.addAttribute("year", year);
    model.addAttribute(
        "thYear", new ThYear(year, "title", Map.of(now().plusMonths(3), RED), Map.of(RED, "Off")));
    return "calendar";
  }
}
