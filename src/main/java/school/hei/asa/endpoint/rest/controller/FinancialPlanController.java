package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.hei.asa.service.FinancialPlanService;

@AllArgsConstructor
@RestController
public class FinancialPlanController {

  private final FinancialPlanService financialPlanService;

  @GetMapping(value = "/financial-plan")
  public String financialPlan(@RequestParam int year) {
    var fp = financialPlanService.financialPlan(year);
    return fp.toString();
  }
}
