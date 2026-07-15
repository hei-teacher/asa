package school.hei.asa.model.contract.cas;

import static gen.patrimoine.modele.Devise.MGA;
import static java.util.stream.Collectors.toSet;

import gen.patrimoine.cas.Cas;
import gen.patrimoine.cas.CasSet;
import gen.patrimoine.cas.CasSetAnalyzer;
import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.Devise;
import gen.patrimoine.modele.Personne;
import gen.patrimoine.modele.possession.Compte;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.Getter;
import school.hei.asa.model.contract.Contract;

public class ContractsToCasSet implements Function<Set<Contract>, CasSet> {

  private final Compte compteCompany;
  @Getter private final Cas companyCas;
  private static final Devise DEVISE = MGA;

  public ContractsToCasSet() {
    this.compteCompany = new Compte("Compte-company", LocalDate.MIN, new Argent(0, DEVISE));
    this.companyCas =
        new CompanyCas(
            LocalDate.MIN, LocalDate.MAX, new Personne("Company"), compteCompany, DEVISE);
  }

  @Getter private final Map<Contract, Exception> koContracts = new HashMap<>();

  @Override
  public CasSet apply(Set<Contract> contracts) {
    Set<Cas> setOfCas =
        contracts.stream()
            .map(c -> new ContractToCas(compteCompany, DEVISE, koContracts).apply(c))
            .collect(toSet());
    setOfCas.add(companyCas);

    var objectif =
        new Argent(
            0,
            DEVISE);
    var casSet = new CasSet(setOfCas, objectif);
    new CasSetAnalyzer()
        .accept(casSet);

    return casSet;
  }
}
