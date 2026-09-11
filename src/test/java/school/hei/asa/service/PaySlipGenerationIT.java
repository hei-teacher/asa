package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.YearMonth;
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
    // IRSA (tranche 10%) = 45 000
    assertBigDecimalEquals(45000, paySlip.deductionTotalAmount());
    assertBigDecimalEquals(54000, paySlip.deductionAndTaxTotal());
    // 450 000 - 9 000 (cotisations) - 45 000 (IRSA)
    assertBigDecimalEquals(396000, paySlip.netAmount());
  }

  @Test
  void includes_taxable_earned_credits_in_the_taxable_base() {
    var paySlip = invoiceService.generatePaySlip(payslipWorker(), YearMonth.of(2026, 4));

    // 2 jours d'absence : (450 000 / 24) x 100% x 2 = 37 500, ajoutes au salaire de base
    assertBigDecimalEquals(487500, paySlip.taxableGrossAmount());
    assertBigDecimalEquals(9750, paySlip.employeeTotalTaxAmount());
    assertBigDecimalEquals(48750, paySlip.deductionTotalAmount());
    assertBigDecimalEquals(429000, paySlip.netAmount());
    assertEquals(1, paySlip.credits().size());
    assertEquals("ABSENCE", paySlip.credits().getFirst().getCredit().getCreditCode());
  }

  @Test
  void a_month_with_no_fixture_data_still_resolves_with_zero_credits() {
    var paySlip = invoiceService.generatePaySlip(payslipWorker(), YearMonth.of(2020, 1));

    assertEquals(0, paySlip.credits().size());
    assertBigDecimalEquals(450000, paySlip.taxableGrossAmount());
  }

  private void assertBigDecimalEquals(long expected, BigDecimal actual) {
    assertEquals(
        0,
        BigDecimal.valueOf(expected).compareTo(actual),
        () -> "expected " + expected + " but was " + actual);
  }
}
