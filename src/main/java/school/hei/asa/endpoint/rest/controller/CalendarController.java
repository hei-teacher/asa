package school.hei.asa.endpoint.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import school.hei.asa.endpoint.rest.model.th.ThYear;

@Controller
public class CalendarController {

  @GetMapping("/calendar")
  public String getMonthCalendar(Model model) {
    var year = 2025;
    model.addAttribute("year", year);
    model.addAttribute("thYear", new ThYear(year));
    return "calendar";
  }
}
