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
import static java.util.stream.Collectors.joining;

import gen.patrimoine.modele.Argent;
import java.time.Month;
import java.util.Map;
import school.hei.asa.model.contract.Contract;

public record FinancialPlan(Map<Month, Argent> plannedCost, Map<Contract, Exception> koContracts) {

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
        ],
        koContracts = %s
        """,
        plannedCost.get(JANUARY).ppMontant(),
        plannedCost.get(FEBRUARY).ppMontant(),
        plannedCost.get(MARCH).ppMontant(),
        plannedCost.get(APRIL).ppMontant(),
        plannedCost.get(MAY).ppMontant(),
        plannedCost.get(JUNE).ppMontant(),
        plannedCost.get(JULY).ppMontant(),
        plannedCost.get(AUGUST).ppMontant(),
        plannedCost.get(SEPTEMBER).ppMontant(),
        plannedCost.get(OCTOBER).ppMontant(),
        plannedCost.get(NOVEMBER).ppMontant(),
        plannedCost.get(DECEMBER).ppMontant(),
        pp(koContracts));
  }

  private String pp(Map<Contract, Exception> koContracts) {
    return koContracts.keySet().stream()
        .map(
            c ->
                String.format(
                    "[%s,%s] %s",
                    c.worker().name(), c.entranceInstant(), koContracts.get(c).getMessage()))
        .collect(joining(", "));
  }
}
