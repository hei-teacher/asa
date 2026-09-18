package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.Worker;

class PaySlipGenerationIT extends FacadeIT {
  @Autowired InvoiceService invoiceService;

  // salaire de base : 450 000 Ar (fixture V101_6, tranche IRSA 400 000,01-500 000 = 10%)
  private Worker payslipWorker() {
    return new Worker("W-PAYSLIP-01", "Rina Rakoto", "", "", "", "", "", "", null, null);
  }

  @Test
  void generates_a_payslip_with_no_earned_credits() {
    var paySlip = invoiceService.generatePaySlip(payslipWorker(), YearMonth.of(2026, 3));

    assertBigDecimalEquals(450000, paySlip.grossAmount());
    assertBigDecimalEquals(450000, paySlip.taxableGrossAmount());
    // CNAPS 1% + OSTIE 1% = 4 500 + 4 500
    assertBigDecimalEquals(9000, paySlip.employeeTotalTaxAmount());
    // CNAPS 13% + OSTIE 5% + FMFP 1% = 58 500 + 22 500 + 4 500
    assertBigDecimalEquals(85500, paySlip.employerTotalTaxAmount());
    // IRSA (tranche 10%) sur le revenu net imposable (450 000 - 9 000 = 441 000) = 44 100
    assertBigDecimalEquals(44100, paySlip.deductionTotalAmount());
    assertBigDecimalEquals(53100, paySlip.deductionAndTaxTotal());
    // 450 000 - 9 000 (cotisations) - 44 100 (IRSA)
    assertBigDecimalEquals(396900, paySlip.netAmount());
  }

  @Test
  void includes_taxable_earned_credits_in_the_taxable_base() {
    var paySlip = invoiceService.generatePaySlip(payslipWorker(), YearMonth.of(2026, 4));

    // 2 jours d'absence : (450 000 / 24) x 100% x 2 = 37 500, ajoutes au salaire de base
    assertBigDecimalEquals(487500, paySlip.taxableGrossAmount());
    assertBigDecimalEquals(9750, paySlip.employeeTotalTaxAmount());
    // IRSA (tranche 10%) sur le revenu net imposable (487 500 - 9 750 = 477 750) = 47 775
    assertBigDecimalEquals(47775, paySlip.deductionTotalAmount());
    assertBigDecimalEquals(429975, paySlip.netAmount());
    assertEquals(1, paySlip.credits().size());
    assertEquals("ABSENCE", paySlip.credits().getFirst().getCredit().getCreditCode());
  }

  @Test
  void a_month_with_no_fixture_data_still_resolves_with_zero_credits() {
    var paySlip = invoiceService.generatePaySlip(payslipWorker(), YearMonth.of(2020, 1));

    assertEquals(0, paySlip.credits().size());
    assertBigDecimalEquals(450000, paySlip.taxableGrossAmount());
  }

  @Test
  void throws_a_clear_error_when_worker_has_no_active_full_time_employee_contract() {
    // W-101 n'a qu'un contrat partnerContractor (jamais fullTimeEmployee)
    var worker = new Worker("W-101", "John", "", "", "", "", "", "", null, null);

    var exception =
        assertThrows(
            NoSuchElementException.class,
            () -> invoiceService.generatePaySlip(worker, YearMonth.of(2026, 3)));

    assertTrue(exception.getMessage().contains("W-101"));
  }

  @Test
  void a_retenue_diverse_that_exceeds_the_gross_salary_no_longer_crashes() {
    // fixture V102_7 : -900 000 sur un brut de 450 000 -> base imposable negative. Tax.resolve()
    // ramene toute base negative a 0 avant de chercher une tranche (cf. TaxTest), donc CNAPS/OSTIE
    // resolvent a 0 et IRSA applique son plancher de tranche 1 (3 000 Ar) au lieu de planter.
    var paySlip = invoiceService.generatePaySlip(payslipWorker(), YearMonth.of(2026, 5));

    assertBigDecimalEquals(-450000, paySlip.taxableGrossAmount());
    assertBigDecimalEquals(0, paySlip.employeeTotalTaxAmount());
    assertBigDecimalEquals(3000, paySlip.deductionTotalAmount());
    assertBigDecimalEquals(-453000, paySlip.netAmount());
  }

  private void assertBigDecimalEquals(long expected, BigDecimal actual) {
    assertEquals(
        0,
        BigDecimal.valueOf(expected).compareTo(actual),
        () -> "expected " + expected + " but was " + actual);
  }
}
