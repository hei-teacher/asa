package school.hei.asa.model.contract.cas;

import gen.patrimoine.cas.Cas;
import gen.patrimoine.modele.evolution.EvolutionPatrimoine;
import gen.patrimoine.visualisation.xchart.GrapheurEvolutionPatrimoine;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;

public class CasToGraph {

  public File apply(Cas cas, int year, int month) {
    var patrimoine = cas.patrimoine();
    var ep =
        new EvolutionPatrimoine(
            patrimoine.nom(),
            patrimoine,
            LocalDate.of(year, month, 1),
            LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth()));
    return new GrapheurEvolutionPatrimoine().apply(ep);
  }
}
