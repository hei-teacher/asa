package school.hei.asa.model.contract.cas;

import static gen.patrimoine.modele.Devise.MGA;
import static java.time.Month.FEBRUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.asa.model.contract.cas.ContractToCasTest.DEC31_2026;
import static school.hei.asa.model.contract.cas.ContractToCasTest.JAN1_2026;
import static school.hei.asa.model.contract.cas.ContractToCasTest.partnerContract;
import static school.hei.asa.model.contract.cas.ContractToCasTest.studentContract;

import gen.patrimoine.modele.Argent;
import gen.patrimoine.modele.possession.Compte;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ContractsToCasSetTest {

  @Test
  void oneMonth_complete_studentAndPartnerContracts() {
    var studentContract = studentContract(JAN1_2026, 11, 50_000);
    var partnerContract = partnerContract(JAN1_2026, 18, 100_000);

    var compteCompany = new Compte("Compte-company", JAN1_2026, new Argent(0, MGA));
    var contractsToCasSet = new ContractsToCasSet();
    var casSet = contractsToCasSet.apply(Set.of(studentContract, partnerContract));

    var companyCas = contractsToCasSet.getCompanyCas();
    assertEquals(
        new Argent(0, MGA),
        companyCas
            .patrimoine()
            .projectionFuture(LocalDate.of(2026, FEBRUARY, 4))
            .getValeurComptable());
    var paidToWorkers = new Argent(50_000 * 11 + 100_000 * 18, MGA).mult(-1);
    assertEquals(
        paidToWorkers,
        companyCas
            .patrimoine()
            .projectionFuture(LocalDate.of(2026, FEBRUARY, 5))
            .getValeurComptable());
    assertEquals(
        paidToWorkers, companyCas.patrimoine().projectionFuture(DEC31_2026).getValeurComptable());
  }
}
