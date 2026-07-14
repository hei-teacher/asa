package school.hei.asa.model.contract.cas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.Devise;
import gen.patrimoine.modele.Personne;
import gen.patrimoine.modele.possession.Compte;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.model.contract.ContractType;

class ContractCasTest {

  private static final Devise DEVISE = Devise.MGA;

  private Worker worker(String code) {
    return new Worker(code, code + "_name", "email", "fullname", "address", "city", "nif", "stat");
  }

  private Contract contract(ContractLevel level, Instant entranceInstant, Duration duration) {
    return new Contract(
        worker("test"),
        "jobTitle",
        level,
        entranceInstant,
        entranceInstant.plus(duration),
        duration,
        "company",
        "bucketKey");
  }

  private ContractCas createCas(
      Contract contract,
      Compte compteCompany,
      Function<ContractType, Integer> contractTypeToDaysPerMonth,
      Map<Contract, Exception> koContracts) {
    var entranceDate = LocalDate.ofInstant(contract.entranceInstant(), ZoneId.systemDefault());
    var personne = new Personne("testPerson");
    return new ContractCas(
        entranceDate,
        entranceDate.plusYears(1),
        personne,
        contract,
        compteCompany,
        contractTypeToDaysPerMonth,
        DEVISE,
        koContracts) {
      @Override
      protected void init() {}

      @Override
      protected void suivi() {}
    };
  }

  private ContractCas createCas(
      Contract contract,
      Compte compteCompany,
      Function<ContractType, Integer> contractTypeToDaysPerMonth) {
    return createCas(contract, compteCompany, contractTypeToDaysPerMonth, new HashMap<>());
  }

  private ContractCas createCas(
      Contract contract, Function<ContractType, Integer> contractTypeToDaysPerMonth) {
    return createCas(
        contract,
        new Compte("company", LocalDate.now(), new Argent(0, DEVISE)),
        contractTypeToDaysPerMonth,
        new HashMap<>());
  }

  private static double montant(Argent argent) {
    return Double.parseDouble(argent.ppMontant());
  }

  @Test
  void nom_returns_contract_toString() {
    var entranceInstant =
        LocalDate.of(2024, 1, 15).atStartOfDay(ZoneId.systemDefault()).toInstant();
    var contract =
        contract(
            new ContractLevel("code", ContractType.fullTimeEmployee, 1000.0, 0.0),
            entranceInstant,
            Duration.ofDays(365));
    var cas = createCas(contract, __ -> 0);

    assertEquals(contract.toString(), cas.nom());
    assertTrue(cas.nom().contains("fullTimeEmployee"));
    assertTrue(cas.nom().contains("test"));
  }

  @Test
  void fte_possessions_creates_one_compte_with_one_flux() {
    var entranceInstant = LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant();
    var monthlyPay = 2000.0;
    var contract =
        contract(
            new ContractLevel("code", ContractType.fullTimeEmployee, monthlyPay, 0.0),
            entranceInstant,
            Duration.ofDays(365));
    var compteCompany = new Compte("company", LocalDate.now(), new Argent(100000, DEVISE));
    var cas = createCas(contract, compteCompany, __ -> 0);

    var possessions = cas.possessions();
    assertEquals(1, possessions.size());

    var compteWorker = (Compte) possessions.iterator().next();
    assertEquals(contract.toString(), compteWorker.nom());
    assertEquals(1, compteWorker.getFluxArgents().size());

    var flux = compteWorker.getFluxArgents().iterator().next();
    assertTrue(montant(flux.getFluxMensuel()) > 0);
    assertEquals(monthlyPay, montant(flux.getFluxMensuel()), 0.001);
    assertEquals(5, flux.getDateOperation());
  }

  @Test
  void partnerContractor_possessions_creates_prorated_payments() {
    var entranceDate = LocalDate.of(2024, 1, 15);
    var entranceInstant = entranceDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    var dailyPay = 50.0;
    var daysPerMonth = 20;
    var contract =
        contract(
            new ContractLevel("code", ContractType.partnerContractor, 0.0, dailyPay),
            entranceInstant,
            Duration.ofDays(60));
    var compteCompany = new Compte("company", LocalDate.now(), new Argent(100000, DEVISE));
    var cas = createCas(contract, compteCompany, type -> daysPerMonth);

    var possessions = cas.possessions();
    var compteWorker = (Compte) possessions.iterator().next();
    var fluxArgents = compteWorker.getFluxArgents();

    assertEquals(4, fluxArgents.size());

    var totalPaid = fluxArgents.stream().mapToDouble(f -> montant(f.getFluxMensuel())).sum();
    assertEquals(dailyPay * 60, totalPaid, 0.001);
  }

  @Test
  void studentContractor_possessions_creates_prorated_payments() {
    var entranceDate = LocalDate.of(2024, 6, 1);
    var entranceInstant = entranceDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    var dailyPay = 30.0;
    var daysPerMonth = 15;
    var contract =
        contract(
            new ContractLevel("code", ContractType.studentContractor, 0.0, dailyPay),
            entranceInstant,
            Duration.ofDays(45));
    var compteCompany = new Compte("company", LocalDate.now(), new Argent(100000, DEVISE));
    var cas = createCas(contract, compteCompany, type -> daysPerMonth);

    var possessions = cas.possessions();
    var compteWorker = (Compte) possessions.iterator().next();
    var fluxArgents = compteWorker.getFluxArgents();

    assertEquals(3, fluxArgents.size());

    var totalPaid = fluxArgents.stream().mapToDouble(f -> montant(f.getFluxMensuel())).sum();
    assertEquals(dailyPay * 45, totalPaid, 0.001);
  }

  @Test
  void fte_entrance_not_on_first_still_pays_full_month() {
    var entranceDate = LocalDate.of(2024, 1, 15);
    var entranceInstant = entranceDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    var monthlyPay = 1000.0;
    var contract =
        contract(
            new ContractLevel("code", ContractType.fullTimeEmployee, monthlyPay, 0.0),
            entranceInstant,
            Duration.ofDays(365));
    var compteCompany = new Compte("company", LocalDate.now(), new Argent(100000, DEVISE));
    var cas = createCas(contract, compteCompany, __ -> 0);

    var possessions = cas.possessions();
    var compteWorker = (Compte) possessions.iterator().next();
    var fluxArgents = compteWorker.getFluxArgents();

    assertEquals(1, fluxArgents.size());
    var flux = fluxArgents.iterator().next();
    assertEquals(monthlyPay, montant(flux.getFluxMensuel()), 0.001);
  }

  @Test
  void koContracts_is_populated_on_failure() {
    var entranceInstant = LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant();
    var contract =
        contract(
            new ContractLevel("code", null, 1000.0, 0.0), entranceInstant, Duration.ofDays(30));
    var compteCompany = new Compte("company", LocalDate.now(), new Argent(100000, DEVISE));
    var koContracts = new HashMap<Contract, Exception>();
    var cas = createCas(contract, compteCompany, __ -> 0, koContracts);

    var possessions = cas.possessions();

    assertNotNull(possessions);
    assertEquals(1, possessions.size());
    assertTrue(koContracts.containsKey(contract));
    assertNotNull(koContracts.get(contract));
  }

  @Test
  void partnerContractor_first_month_is_prorated_when_not_starting_on_first() {
    var entranceDate = LocalDate.of(2024, 1, 20);
    var entranceInstant = entranceDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    var dailyPay = 100.0;
    var daysPerMonth = 20;
    var contract =
        contract(
            new ContractLevel("code", ContractType.partnerContractor, 0.0, dailyPay),
            entranceInstant,
            Duration.ofDays(40));
    var compteCompany = new Compte("company", LocalDate.now(), new Argent(100000, DEVISE));
    var cas = createCas(contract, compteCompany, type -> daysPerMonth);

    var possessions = cas.possessions();
    var compteWorker = (Compte) possessions.iterator().next();
    var fluxArgents = compteWorker.getFluxArgents();

    assertEquals(3, fluxArgents.size());

    var firstFluxMontant = montant(fluxArgents.stream().findFirst().get().getFluxMensuel());
    assertTrue(firstFluxMontant < dailyPay * daysPerMonth);
  }
}
