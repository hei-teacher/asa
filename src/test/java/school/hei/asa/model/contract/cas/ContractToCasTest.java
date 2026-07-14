package school.hei.asa.model.contract.cas;

import static gen.patrimoine.modele.Devise.MGA;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static school.hei.asa.model.contract.ContractType.partnerContractor;
import static school.hei.asa.model.contract.ContractType.studentContractor;

import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.possession.Compte;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;

public class ContractToCasTest {

  public static final LocalDate JAN1_2026 = LocalDate.of(2026, 1, 1);
  public static final LocalDate DEC31_2026 = LocalDate.of(2026, 12, 31);

  public static Contract studentContract(
      LocalDate entranceDate, int durationDays, double dailyPay) {
    return new Contract(
        new Worker(
            "W-001", "Student", "student@test.com", "Student", "Addr", "City", "NIF", "STAT"),
        "Student Job",
        new ContractLevel("STD", studentContractor, null, dailyPay),
        entranceDate.atStartOfDay(java.time.ZoneOffset.UTC).toInstant(),
        null,
        Duration.ofDays(durationDays),
        "Company",
        null);
  }

  public static Contract partnerContract(
      LocalDate entranceDate, int durationDays, double dailyPay) {
    return new Contract(
        new Worker(
            "W-002", "Partner", "partner@test.com", "Partner", "Addr", "City", "NIF", "STAT"),
        "Partner Job",
        new ContractLevel("PTN", partnerContractor, null, dailyPay),
        entranceDate.atStartOfDay(java.time.ZoneOffset.UTC).toInstant(),
        null,
        Duration.ofDays(durationDays),
        "Company",
        null);
  }

  @Test
  void apply_returns_contractCas_for_student_contractor() {
    var compteCompany = new Compte("Compte-company", LocalDate.MIN, new Argent(0, MGA));
    var contractToCas = new ContractToCas(compteCompany, MGA);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John Doe", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("STD", studentContractor, null, 25_000.0),
            Instant.parse("2025-01-15T00:00:00Z"),
            null,
            Duration.ofDays(100),
            "Company",
            null);

    var result = contractToCas.apply(contract);

    assertInstanceOf(ContractCas.class, result);
  }

  @Test
  void apply_creates_contractCas_with_personne() {
    var compteCompany = new Compte("Compte-company", LocalDate.MIN, new Argent(0, MGA));
    var contractToCas = new ContractToCas(compteCompany, MGA);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John Doe", "Addr", "City", "NIF", "STAT");
    var contract =
        new Contract(
            worker,
            "Job",
            new ContractLevel("STD", studentContractor, null, 25_000.0),
            Instant.parse("2025-01-15T00:00:00Z"),
            null,
            Duration.ofDays(100),
            "Company",
            null);

    var result = contractToCas.apply(contract);

    assertInstanceOf(ContractCas.class, result);
  }
}
