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
    log.info("FinancialPlanController#financialPlan: fp={}", fp);
    model.addAttribute("currentYear", defaultYearValue);
    model.addAttribute("months", Arrays.stream(Month.values()).toList());
    model.addAttribute("plannedCost", fp.plannedCost());
    model.addAttribute("executedCost", fp.executedCost());
    model.addAttribute("differenceFromPlanned", fp.getDiff());
    model.addAttribute("koContracts", fp.koContracts());
    return "financial-plan";
  }
}
