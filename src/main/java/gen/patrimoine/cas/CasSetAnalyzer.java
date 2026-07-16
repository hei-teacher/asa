package gen.patrimoine.cas;

import static gen.patrimoine.modele.Devise.MGA;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.objectif.ObjectifNonAtteint;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CasSetAnalyzer implements Consumer<CasSet> {
  private final int closeOperation;

  public static void main(String[] args) {
    var casSet = new CasSet(Set.of(), new Argent(0, MGA));
    new CasSetAnalyzer().accept(casSet);
  }

  public CasSetAnalyzer() {
    this(EXIT_ON_CLOSE);
  }

  @Override
  public void accept(CasSet casSet) {
    var aCas = casSet.set().stream().toList().getFirst();
    var patrimoineTout = new ToutCas(aCas.getAjd(), aCas.getFinSimulation(), casSet);
    verifie(patrimoineTout);
    visualise(casSet.set(), patrimoineTout);
  }

  private void visualise(Set<Cas> set, ToutCas patrimoineTout) {
    // Purposefully removed from original patrimoine version, as we don't need Swing visualizer
  }

  private static void verifie(ToutCas patrimoineTout) {
    var objectifsNonAtteints = patrimoineTout.verifier();
    if (!objectifsNonAtteints.isEmpty()) {
      throw new RuntimeException(
          "Objectifs non atteints : "
              + objectifsNonAtteints.stream()
                  .sorted(
                      (lhs, rhs) -> {
                        if (lhs.objectif().t().equals(rhs.objectif().t())) {
                          return 0;
                        }
                        return lhs.objectif().t().isBefore(rhs.objectif().t()) ? -1 : 1;
                      })
                  .map(ObjectifNonAtteint::prettyPrint)
                  .collect(Collectors.joining("\n")));
    }
  }
}
