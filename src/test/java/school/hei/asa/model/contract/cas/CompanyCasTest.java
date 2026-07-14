package school.hei.asa.model.contract.cas;

import static gen.patrimoine.modele.Devise.MGA;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.Personne;
import gen.patrimoine.modele.possession.Compte;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CompanyCasTest {

  @Test
  void company_cas_has_correct_name_and_devise() {
    var compte = new Compte("Compte-company", LocalDate.MIN, new Argent(0, MGA));
    var companyCas =
        new CompanyCas(LocalDate.MIN, LocalDate.MAX, new Personne("Company"), compte, MGA);

    assertEquals("Compte-company", companyCas.nom());
    assertEquals(MGA, companyCas.devise());
    assertEquals(1, companyCas.possessions().size());
    assertEquals(compte, companyCas.possessions().iterator().next());
  }
}
