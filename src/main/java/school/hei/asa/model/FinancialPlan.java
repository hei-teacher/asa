package school.hei.asa.model;

import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;

import gen.patrimoine.modele.Argent;
import java.time.Month;
import java.util.Map;

public record FinancialPlan(Map<Month, Argent> cost) {

  @Override
  public String toString() {
    return String.format(
        """
        cost = [
          jan: %s,
          feb: %s,
          mar: %s,
          apr: %s,
          may: %s,
          jun: %s,
          jul: %s,
          aug: %s,
          sep: %s,
          oct: %s,
          nov: %s,
          dec: %s
        ]
        """,
        cost.get(JANUARY).ppMontant(),
        cost.get(FEBRUARY).ppMontant(),
        cost.get(MARCH).ppMontant(),
        cost.get(APRIL).ppMontant(),
        cost.get(MAY).ppMontant(),
        cost.get(JUNE).ppMontant(),
        cost.get(JULY).ppMontant(),
        cost.get(AUGUST).ppMontant(),
        cost.get(SEPTEMBER).ppMontant(),
        cost.get(OCTOBER).ppMontant(),
        cost.get(NOVEMBER).ppMontant(),
        cost.get(DECEMBER).ppMontant());
  }
}
