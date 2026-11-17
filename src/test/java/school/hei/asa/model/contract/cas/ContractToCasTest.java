package school.hei.asa.model.contract.cas;

import static gen.patrimoine.modele.Devise.MGA;
import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static school.hei.asa.model.contract.ContractType.fullTimeEmployee;
import static school.hei.asa.model.contract.ContractType.partnerContractor;
import static school.hei.asa.model.contract.ContractType.studentContractor;

import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.possession.Compte;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;

public class ContractToCasTest {

  public static final LocalDate JAN1_2026 = LocalDate.of(2026, JANUARY, 1);
  static final LocalDate DEC31_2026 = LocalDate.of(2026, DECEMBER, 31);

  @Test
  void oneMonth_complete_studentContract() {
    var contract = studentContract(JAN1_2026, 11, 50_000);

    var compteCompany = new Compte("Compte-company", JAN1_2026, new Argent(0, MGA));
    var cas = new ContractToCas(compteCompany, MGA, Map.of()).apply(contract);

    assertEquals(
        new Argent(0, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 30)).getValeurComptable());
    assertEquals(
        new Argent(550_000, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 31)).getValeurComptable());
    assertEquals(
        new Argent(550_000, MGA),
        cas.patrimoine().projectionFuture(DEC31_2026).getValeurComptable());

    assertEquals(
        new Argent(-550_000, MGA), compteCompany.projectionFuture(DEC31_2026).valeurComptable());
  }

  @Test
  void oneYear_fteContract() {
    var contract = fteContract(JAN1_2026, 500_000);

    var compteCompany = new Compte("Compte-company", JAN1_2026, new Argent(0, MGA));
    var cas = new ContractToCas(compteCompany, MGA, Map.of()).apply(contract);

    assertEquals(
        new Argent(0, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 27)).getValeurComptable());
    assertEquals(
        new Argent(500_000, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 28)).getValeurComptable());
    assertEquals(
        new Argent(6_000_000, MGA),
        cas.patrimoine().projectionFuture(DEC31_2026).getValeurComptable());

    assertEquals(
        new Argent(-6_000_000, MGA), compteCompany.projectionFuture(DEC31_2026).valeurComptable());
  }

  @Test
  void twoMonths_eachPartial_studentContract() {
    var dailyPay = 50_000;
    var contract = studentContract(LocalDate.of(2026, JANUARY, 15), 11, dailyPay);

    var compteCompany = new Compte("Compte-company", JAN1_2026, new Argent(0, MGA));
    var cas = new ContractToCas(compteCompany, MGA).apply(contract);

    assertEquals(
        new Argent(0, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 30)).getValeurComptable());
    assertEquals(
        new Argent(dailyPay * 6, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 31)).getValeurComptable());
    assertEquals(
        new Argent(dailyPay * 11, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, FEBRUARY, 28)).getValeurComptable());
    assertEquals(
        new Argent(dailyPay * 11, MGA),
        cas.patrimoine().projectionFuture(DEC31_2026).getValeurComptable());
  }

  @Test
  void fourMonths_studentContract() {
    var dailyPay = 50_000;
    var contract = studentContract(LocalDate.of(2026, JANUARY, 15), 33, dailyPay);

    var compteCompany = new Compte("Compte-company", JAN1_2026, new Argent(0, MGA));
    var cas = new ContractToCas(compteCompany, MGA).apply(contract);

    assertEquals(
        new Argent(0, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 30)).getValeurComptable());
    assertEquals(
        new Argent(dailyPay * 6, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 31)).getValeurComptable());
    assertEquals(
        new Argent(dailyPay * (6 + 11), MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, FEBRUARY, 28)).getValeurComptable());
    assertEquals(
        new Argent(dailyPay * (6 + 11 + 11), MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, MARCH, 31)).getValeurComptable());
    assertEquals(
        new Argent(dailyPay * 33, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, APRIL, 30)).getValeurComptable());

    assertEquals(
        new Argent(dailyPay * 33, MGA),
        cas.patrimoine().projectionFuture(DEC31_2026).getValeurComptable());
  }

  @Test
  void oneMonth_complete_partnerContract() {
    var contract = partnerContract(JAN1_2026, 18, 100_000);

    var compteCompany = new Compte("Compte-company", JAN1_2026, new Argent(0, MGA));
    var cas = new ContractToCas(compteCompany, MGA).apply(contract);

    assertEquals(
        new Argent(0, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 30)).getValeurComptable());
    assertEquals(
        new Argent(1800000, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 31)).getValeurComptable());
    assertEquals(
        new Argent(1800000, MGA),
        cas.patrimoine().projectionFuture(DEC31_2026).getValeurComptable());
  }

  @Test
  void studentContract_ended_early_stops_paying_after_end_month() {
    var dailyPay = 50_000;
    var contract =
        withEnd(studentContract(JAN1_2026, 33, dailyPay), LocalDate.of(2026, FEBRUARY, 14));

    var compteCompany = new Compte("Compte-company", JAN1_2026, new Argent(0, MGA));
    var cas = new ContractToCas(compteCompany, MGA).apply(contract);

    assertEquals(
        new Argent(dailyPay * 11, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, JANUARY, 31)).getValeurComptable());
    // February is prorated: 14 of 28 days of the 11 days quota
    assertEquals(
        new Argent(dailyPay * (11 + 5), MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, FEBRUARY, 28)).getValeurComptable());
    assertEquals(
        new Argent(dailyPay * (11 + 5), MGA),
        cas.patrimoine().projectionFuture(DEC31_2026).getValeurComptable());
  }

  @Test
  void fteContract_ended_stops_paying_after_end_month() {
    var contract = withEnd(fteContract(JAN1_2026, 500_000), LocalDate.of(2026, MARCH, 15));

    var compteCompany = new Compte("Compte-company", JAN1_2026, new Argent(0, MGA));
    var cas = new ContractToCas(compteCompany, MGA, Map.of()).apply(contract);

    // January, February and March costs are assigned to their own month
    assertEquals(
        new Argent(1_500_000, MGA),
        cas.patrimoine().projectionFuture(LocalDate.of(2026, MARCH, 31)).getValeurComptable());
    assertEquals(
        new Argent(1_500_000, MGA),
        cas.patrimoine().projectionFuture(DEC31_2026).getValeurComptable());
  }

  static Contract withEnd(Contract contract, LocalDate endDate) {
    return new Contract(
        contract.worker(),
        contract.jobTitle(),
        contract.level(),
        contract.entranceInstant(),
        toInstant(endDate),
        contract.duration(),
        contract.company(),
        contract.contractBucketKey());
  }

  public static Contract studentContract(LocalDate entranceDate, int nbDays, int dailyPay) {
    return new Contract(
        mock(Worker.class),
        "jobTitle",
        new ContractLevel("level", studentContractor, null, (double) dailyPay, null),
        toInstant(entranceDate),
        null,
        Duration.ofDays(nbDays),
        "company",
        "contractBucketKey");
  }

  static Contract partnerContract(LocalDate entranceDate, int nbDays, int dailyPay) {
    return new Contract(
        mock(Worker.class),
        "jobTitle",
        new ContractLevel("level", partnerContractor, null, (double) dailyPay, null),
        toInstant(entranceDate),
        null,
        Duration.ofDays(nbDays),
        "company",
        "contractBucketKey");
  }

  static Contract fteContract(LocalDate entranceDate, int monthlyPay) {
    return new Contract(
        mock(Worker.class),
        "jobTitle",
        new ContractLevel("level", fullTimeEmployee, (double) monthlyPay, null, null),
        toInstant(entranceDate),
        null,
        null,
        "company",
        "contractBucketKey");
  }

  static Instant toInstant(LocalDate date) {
    return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
  }
}
