package gen.patrimoine.modele.possession;

import static gen.patrimoine.modele.possession.TypeAgregat.OBLIGATION;

import gen.patrimoine.modele.Argent;
import java.time.LocalDate;

public final class Dette extends Compte {

  public Dette(String nom, LocalDate t, Argent valeurComptable) {
    super(nom, t, valeurComptable);
    if (valeurComptable.gt(0)) {
      throw new IllegalArgumentException();
    }
  }

  private Dette(Compte compte) {
    this(compte.nom, compte.t, compte.valeurComptable);
  }

  @Override
  public Dette projectionFuture(LocalDate tFutur) {
    return new Dette(super.projectionFuture(tFutur));
  }

  @Override
  public TypeAgregat typeAgregat() {
    return OBLIGATION;
  }
}
