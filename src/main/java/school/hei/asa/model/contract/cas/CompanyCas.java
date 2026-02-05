package school.hei.asa.model.contract.cas;

import gen.patrimoine.cas.Cas;
import gen.patrimoine.modele.Devise;
import gen.patrimoine.modele.Personne;
import gen.patrimoine.modele.possession.Compte;
import gen.patrimoine.modele.possession.Possession;
import java.time.LocalDate;
import java.util.Set;

public class CompanyCas extends Cas {
  private final Compte compteCompany;
  private final Devise devise;

  protected CompanyCas(
      LocalDate ajd,
      LocalDate finSimulation,
      Personne possesseur,
      Compte compteCompany,
      Devise devise) {
    super(ajd, finSimulation, possesseur);
    this.compteCompany = compteCompany;
    this.devise = devise;
  }

  @Override
  protected Devise devise() {
    return devise;
  }

  @Override
  protected String nom() {
    return "Compte-company";
  }

  @Override
  protected void init() {}

  @Override
  protected void suivi() {}

  @Override
  public Set<Possession> possessions() {
    return Set.of(compteCompany);
  }
}
