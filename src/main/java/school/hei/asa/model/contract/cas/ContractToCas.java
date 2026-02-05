package school.hei.asa.model.contract.cas;

import static java.time.LocalDate.now;

import gen.patrimoine.cas.Cas;
import gen.patrimoine.modele.Devise;
import gen.patrimoine.modele.Personne;
import gen.patrimoine.modele.possession.Compte;
import java.time.LocalDate;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import school.hei.asa.model.contract.Contract;

@AllArgsConstructor
public class ContractToCas implements Function<Contract, Cas> {

  private final Compte compteCompany;
  private final Devise devise;

  private static final int DAYS_PER_MONTH_PARTNER = 18;
  private static final int DAYS_PER_MONTH_STUDENT = 11;
  private static final int DAYS_PER_MONTH_FTE = 20;

  @Override
  public Cas apply(Contract contract) {
    var worker = contract.worker();
    var personne = new Personne(worker.code() + " - " + worker.name());
    return new ContractCas(
        now(),
        LocalDate.MAX,
        personne,
        contract,
        compteCompany,
        t ->
            switch (t) {
              case partnerContractor -> DAYS_PER_MONTH_PARTNER;
              case studentContractor -> DAYS_PER_MONTH_STUDENT;
              case fullTimeEmployee -> DAYS_PER_MONTH_FTE;
            },
        devise);
  }
}
