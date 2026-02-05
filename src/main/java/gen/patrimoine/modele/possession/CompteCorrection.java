package gen.patrimoine.modele.possession;

import static gen.patrimoine.modele.possession.TypeAgregat.CORRECTION;

import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.Devise;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public final class CompteCorrection extends Possession {

  final Compte compte;

  public CompteCorrection(String nom, Devise devise) {
    this(
        "Correction[" + nom + "]",
        new Compte(
            String.format("Correction.Argent[%s]", nom), LocalDate.MIN, new Argent(0, devise)));
  }

  private CompteCorrection(String nom, Compte compte) {
    super(nom, compte.t, compte.valeurComptable);
    this.compte = compte;
  }

  @Override
  public CompteCorrection projectionFuture(LocalDate tFutur) {
    return new CompteCorrection(nom, compte.projectionFuture(tFutur));
  }

  @Override
  public TypeAgregat typeAgregat() {
    return CORRECTION;
  }
}
