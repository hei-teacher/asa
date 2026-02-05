package gen.patrimoine.modele.possession;

import static gen.patrimoine.modele.Argent.euro;
import static gen.patrimoine.modele.possession.TypeAgregat.PATRIMOINE;

import gen.patrimoine.modele.Personne;
import java.time.LocalDate;
import lombok.Getter;
import lombok.experimental.Accessors;

public final class PersonneMorale extends Possession {

  @Accessors(fluent = true)
  @Getter
  private final Personne personne;

  public PersonneMorale(String nom) {
    super(nom, LocalDate.MIN, euro(0));
    personne = new Personne(nom);
  }

  @Override
  public Possession projectionFuture(LocalDate tFutur) {
    return new GroupePossession(
        personne.nom() + " " + tFutur,
        devise(),
        tFutur,
        personne.patrimoine(devise(), tFutur).getPossessions());
  }

  @Override
  public TypeAgregat typeAgregat() {
    return PATRIMOINE;
  }
}
