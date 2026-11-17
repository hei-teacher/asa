package school.hei.asa.endpoint.rest.controller;

import static java.time.LocalDate.now;

import java.time.Month;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.service.FinancialPlanService;

@Slf4j
@AllArgsConstructor
@Controller
public class FinancialPlanController {

  private final FinancialPlanService financialPlanService;

  @GetMapping(value = "/financial-plan")
  public String financialPlan(@RequestParam(required = false) Integer year, Model model) {
    var defaultYearValue = year != null ? year : now().getYear();
    var fp = financialPlanService.financialPlan(defaultYearValue);

    model.addAttribute("currentYear", defaultYearValue);
    model.addAttribute("months", Arrays.stream(Month.values()).toList());
    model.addAttribute("plannedCost", fp.plannedCost());
    model.addAttribute("executedCost", fp.executedCost());
    model.addAttribute("differenceFromPlanned", fp.getDifferenceFromPlanned());
    model.addAttribute("koContracts", fp.koContracts());
    model.addAttribute("ytdPlannedCost", fp.getYearToDatePlannedCost());
    model.addAttribute("ytdExecutedCost", fp.getYearToDateExecutedCost());
    model.addAttribute("ytdDifferenceFromPlanned", fp.getYearToDateDifferenceFromPlanned());
    model.addAttribute("ytdLastMonth", ytdLastMonth(defaultYearValue));

    return "financial-plan";
  }

  /** Last month (1-12) whose year-to-date is shown: none for future years, all for past ones. */
  private static int ytdLastMonth(int year) {
    var today = now();
    if (year < today.getYear()) {
      return 12;
    }
    return year == today.getYear() ? today.getMonthValue() : 0;
  }
}
