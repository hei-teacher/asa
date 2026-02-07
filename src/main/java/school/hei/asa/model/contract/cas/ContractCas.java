package school.hei.asa.model.contract.cas;

import static java.time.ZoneId.systemDefault;

import gen.patrimoine.cas.Cas;
import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.Devise;
import gen.patrimoine.modele.Personne;
import gen.patrimoine.modele.possession.Compte;
import gen.patrimoine.modele.possession.Possession;
import gen.patrimoine.modele.possession.TransfertArgent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractType;

@Slf4j
public class ContractCas extends Cas {
  private final Contract contract;
  private final Compte compteCompany;
  private final Function<ContractType, Integer> contractTypeToDaysPerMonth;
  private final Devise devise;
  private final Map<Contract, Exception> koContracts;

  private static final int PAY_DAY = 5;

  protected ContractCas(
      LocalDate ajd,
      LocalDate finSimulation,
      Personne personne,
      Contract contract,
      Compte compteCompany,
      Function<ContractType, Integer> contractTypeToDaysPerMonth,
      Devise devise,
      Map<Contract, Exception> koContracts) {
    super(ajd, finSimulation, personne);
    this.contract = contract;
    this.compteCompany = compteCompany;
    this.contractTypeToDaysPerMonth = contractTypeToDaysPerMonth;
    this.devise = devise;
    this.koContracts = koContracts;
  }

  @Override
  protected Devise devise() {
    return devise;
  }

  @Override
  protected String nom() {
    return contract.toString();
  }

  @Override
  protected void init() {}

  @Override
  public Set<Possession> possessions() {
    var compteWorker = new Compte(contract.toString(), entranceDate(), new Argent(0, devise));

    try {
      payAllMonths(compteWorker);
    } catch (Exception e) {
      koContracts.put(contract, e);
    }

    return Set.of(compteWorker);
  }

  @Override
  protected void suivi() {}

  private void payAllMonths(Compte compteWorker) {
    // First month, potentially partial
    var daysToPayMonth1 = daysToPayMonth1();
    payMonthNth(1, compteWorker, daysToPayMonth1);

    // Intermediate months, all full
    var remainingDays = contract.duration().toDays() - daysToPayMonth1;
    var monthNth = 2;
    var daysPerMonth = contractTypeToDaysPerMonth.apply(contract.level().type());
    while (remainingDays > daysPerMonth) {
      payMonthNth(monthNth, compteWorker, daysPerMonth);
      remainingDays -= daysPerMonth;
      monthNth++;
    }

    // Last month, potentially partial
    payMonthNth(monthNth, compteWorker, remainingDays);
  }

  private void payMonthNth(int monthNth, Compte compteWorker, double daysToPay) {
    var entranceDate = entranceDate();
    var payDateOfMonth0 =
        LocalDate.of(entranceDate.getYear(), entranceDate.getMonthValue(), PAY_DAY);

    new TransfertArgent(
        String.format("[Mois %s] %s", monthNth, contract),
        compteCompany,
        compteWorker,
        payDateOfMonth0.plusMonths(monthNth),
        new Argent(contract.level().dailyPay() * daysToPay, devise));
  }

  private int daysToPayMonth1() {
    var contractLevel = contract.level();
    var daysToWorkPerMonth = contractTypeToDaysPerMonth.apply(contractLevel.type());

    var entranceDate = entranceDate();
    int year = entranceDate.getYear();
    int month = entranceDate.getMonthValue();
    var lengthOfMonth1 = YearMonth.of(year, month).lengthOfMonth();

    var month1Completeness = (lengthOfMonth1 - entranceDate.getDayOfMonth() + 1.) / lengthOfMonth1;
    return (int) (daysToWorkPerMonth * month1Completeness);
  }

  private LocalDate entranceDate() {
    return LocalDate.ofInstant(contract.entranceInstant(), systemDefault());
  }
}
