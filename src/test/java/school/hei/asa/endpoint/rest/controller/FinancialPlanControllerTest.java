package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.Devise;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.FinancialPlan;
import school.hei.asa.service.FinancialPlanService;

class FinancialPlanControllerTest {

  private final FinancialPlanService financialPlanService = mock(FinancialPlanService.class);
  private final FinancialPlanController controller =
      new FinancialPlanController(financialPlanService);

  @Test
  void financialPlan_returns_expected_string() {
    int year = 2026;
    var fp = new FinancialPlan(fullMonthMap(1000.0), fullMonthMap(500.0), Map.of());
    when(financialPlanService.financialPlan(year)).thenReturn(fp);

    var result = controller.financialPlan(year);

    assertEquals(fp.toString(), result);
  }

  @Test
  void financialPlan_with_year_zero() {
    int year = 0;
    var fp = new FinancialPlan(fullMonthMap(0.0), fullMonthMap(0.0), Map.of());
    when(financialPlanService.financialPlan(year)).thenReturn(fp);

    var result = controller.financialPlan(year);

    assertEquals(fp.toString(), result);
  }

  private Map<Month, Argent> fullMonthMap(double amount) {
    var map = new HashMap<Month, Argent>();
    for (var month : Month.values()) {
      map.put(month, new Argent(amount, Devise.MGA));
    }
    return map;
  }
}
