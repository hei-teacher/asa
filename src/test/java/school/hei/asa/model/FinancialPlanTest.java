package school.hei.asa.model;

import static gen.patrimoine.modele.Devise.MGA;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gen.patrimoine.modele.Argent;
import java.time.Month;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FinancialPlanTest {

  @Test
  void year_to_date_sums_months_from_january() {
    var planned = perMonth(100);
    var executed = perMonth(60);
    executed.put(FEBRUARY, new Argent(160, MGA));

    var financialPlan = new FinancialPlan(planned, executed, Map.of());

    assertEquals(new Argent(100, MGA), financialPlan.getYearToDatePlannedCost().get(JANUARY));
    assertEquals(new Argent(300, MGA), financialPlan.getYearToDatePlannedCost().get(MARCH));
    assertEquals(new Argent(1_200, MGA), financialPlan.getYearToDatePlannedCost().get(DECEMBER));
    assertEquals(new Argent(280, MGA), financialPlan.getYearToDateExecutedCost().get(MARCH));
    assertEquals(
        new Argent(40, MGA), financialPlan.getYearToDateDifferenceFromPlanned().get(JANUARY));
    assertEquals(
        new Argent(-20, MGA), financialPlan.getYearToDateDifferenceFromPlanned().get(FEBRUARY));
    assertEquals(
        new Argent(20, MGA), financialPlan.getYearToDateDifferenceFromPlanned().get(MARCH));
  }

  private static Map<Month, Argent> perMonth(double amount) {
    var map = new EnumMap<Month, Argent>(Month.class);
    for (Month month : Month.values()) {
      map.put(month, new Argent(amount, MGA));
    }
    return map;
  }
}
