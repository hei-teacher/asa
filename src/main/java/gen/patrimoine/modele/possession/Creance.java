package gen.patrimoine.modele.possession;

import static gen.patrimoine.modele.possession.TypeAgregat.OBLIGATION;

import gen.patrimoine.modele.Argent;
import java.time.LocalDate;

public final class Creance extends Compte {

  public Creance(String nom, LocalDate t, Argent valeurComptable) {
    super(nom, t, valeurComptable);
    if (valeurComptable.lt(0)) {
      throw new IllegalArgumentException(this.toString());
    }
  }

  private Creance(Compte compte) {
    this(compte.nom, compte.t, compte.valeurComptable);
  }

  @Override
  public Creance projectionFuture(LocalDate tFutur) {
    return new Creance(super.projectionFuture(tFutur));
  }

  @Override
  public TypeAgregat typeAgregat() {
    return OBLIGATION;
  }
}
