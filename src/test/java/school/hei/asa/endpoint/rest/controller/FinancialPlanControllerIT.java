package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.Devise;
import java.time.Month;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.Model;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.FinancialPlan;
import school.hei.asa.service.FinancialPlanService;

class FinancialPlanControllerIT extends FacadeIT {

  @Autowired FinancialPlanController subject;

  @MockBean FinancialPlanService financialPlanService;

  Model model;

  @Test
  void oneMonth_complete_studentContract_mockedService() {
    Map<Month, Argent> defaultMonthlyMap = new EnumMap<>(Month.class);
    for (Month month : Month.values()) {
      defaultMonthlyMap.put(month, new Argent(0, Devise.MGA));
    }

    var fakeFinancialPlan = new FinancialPlan(defaultMonthlyMap, defaultMonthlyMap, Map.of());

    when(financialPlanService.financialPlan(2026)).thenReturn(fakeFinancialPlan);

    model = mock(Model.class);
    var viewName = subject.financialPlan(2026, model);

    verify(financialPlanService).financialPlan(2026);
    verify(model).addAttribute(eq("currentYear"), any(Integer.class));
    verify(model).addAttribute(eq("months"), any(List.class));
    verify(model).addAttribute(eq("plannedCost"), any(Map.class));
    verify(model).addAttribute(eq("executedCost"), any(Map.class));
    verify(model).addAttribute(eq("differenceFromPlanned"), any(Map.class));

    verify(model).addAttribute(eq("koContracts"), any(Map.class));

    assertEquals("financial-plan", viewName);
  }
}
