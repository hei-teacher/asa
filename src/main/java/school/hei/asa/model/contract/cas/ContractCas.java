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
      var contractType = contract.level().type();
      switch (contractType) {
        case partnerContractor, studentContractor -> payContractorAllMonths(compteWorker);
        case fullTimeEmployee -> payFTEAllMonths(compteWorker);
        default -> throw new RuntimeException("Unsupported contract type: " + contractType);
      }
    } catch (Exception e) {
      koContracts.put(contract, e);
    }

    return Set.of(compteWorker);
  }

  private void payFTEAllMonths(Compte compteWorker) {
    var entranceDate = entranceDate();
    if (entranceDate.getDayOfMonth() != 1) {
      log.warn(
          "Contract does not start at 1st of month."
              + "No ratio will be paid: assigned cost will be that of a full month."
              + "Contract={}",
          contract.ppId());
    }

    // a worked month is paid on PAY_DAY of the following month
    var lastPayMonthEnd =
        contract.endDate() == null
            ? entranceDate.plusYears(99)
            : YearMonth.from(contract.endDate()).plusMonths(1).atEndOfMonth();
    new TransfertArgent(
        contract.ppId(),
        compteCompany,
        compteWorker,
        LocalDate.of(entranceDate.getYear(), entranceDate.getMonthValue(), 1).plusMonths(1),
        lastPayMonthEnd,
        PAY_DAY,
        new Argent(contract.level().monthlyPay(), devise));
  }

  @Override
  protected void suivi() {}

  private void payContractorAllMonths(Compte compteWorker) {
    var remainingDays = (double) contract.duration().toDays();
    var workedMonth = YearMonth.from(entranceDate());
    var lastWorkedMonth = contract.endDate() == null ? null : YearMonth.from(contract.endDate());
    var monthNth = 1;
    // First and last months are potentially partial, intermediate ones are full
    while (remainingDays > 0
        && (lastWorkedMonth == null || !workedMonth.isAfter(lastWorkedMonth))) {
      var daysToPay = Math.min(contractorDaysToPay(workedMonth), remainingDays);
      payContractorMonthNth(monthNth, compteWorker, daysToPay);
      remainingDays -= daysToPay;
      workedMonth = workedMonth.plusMonths(1);
      monthNth++;
    }
  }

  private void payContractorMonthNth(int monthNth, Compte compteWorker, double daysToPay) {
    var entranceDate = entranceDate();
    var payDateOfMonth0 =
        LocalDate.of(entranceDate.getYear(), entranceDate.getMonthValue(), PAY_DAY);

    new TransfertArgent(
        String.format("[Mois %s] %s", monthNth, contract.ppId()),
        compteCompany,
        compteWorker,
        payDateOfMonth0.plusMonths(monthNth),
        new Argent(contract.level().dailyPay() * daysToPay, devise));
  }

  /** Monthly quota of days, prorated on the calendar days of the month covered by the contract. */
  private int contractorDaysToPay(YearMonth month) {
    var daysToWorkPerMonth = contractTypeToDaysPerMonth.apply(contract.level().type());
    var lengthOfMonth = month.lengthOfMonth();
    var coveredDays = contract.daysCovered(month.atDay(1), month.atEndOfMonth());
    if (coveredDays == lengthOfMonth) {
      return daysToWorkPerMonth;
    }
    return (int) (daysToWorkPerMonth * ((double) coveredDays / lengthOfMonth));
  }

  private LocalDate entranceDate() {
    return LocalDate.ofInstant(contract.entranceInstant(), systemDefault());
  }
}
