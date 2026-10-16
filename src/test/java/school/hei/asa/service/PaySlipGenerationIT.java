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

  private Worker payslipWorker() {
    return new Worker("W-PAYSLIP-01", "Rina Rakoto", "", "", "", "", "", "", null, null);
  }

  @Test
  void generates_a_payslip_with_no_earned_credits() {
    var paySlip = invoiceService.generatePaySlip(payslipWorker(), YearMonth.of(2026, 3));

    assertBigDecimalEquals(450000, paySlip.grossAmount());
    assertBigDecimalEquals(450000, paySlip.taxableGrossAmount());
    assertBigDecimalEquals(9000, paySlip.employeeTotalTaxAmount());
    assertBigDecimalEquals(85500, paySlip.employerTotalTaxAmount());
    assertBigDecimalEquals(6600, paySlip.deductionTotalAmount());
    assertBigDecimalEquals(15600, paySlip.deductionAndTaxTotal());
    assertBigDecimalEquals(434400, paySlip.netAmount());
  }

  @Test
  void includes_taxable_earned_credits_in_the_taxable_base() {
    var paySlip = invoiceService.generatePaySlip(payslipWorker(), YearMonth.of(2026, 4));

    assertBigDecimalEquals(487500, paySlip.taxableGrossAmount());
    assertBigDecimalEquals(9750, paySlip.employeeTotalTaxAmount());
    assertBigDecimalEquals(10270, paySlip.deductionTotalAmount());
    assertBigDecimalEquals(467480, paySlip.netAmount());
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
    var worker = new Worker("W-101", "John", "", "", "", "", "", "", null, null);

    var exception =
        assertThrows(
            NoSuchElementException.class,
            () -> invoiceService.generatePaySlip(worker, YearMonth.of(2026, 3)));

    assertTrue(exception.getMessage().contains("W-101"));
  }

  @Test
  void a_retenue_diverse_that_exceeds_the_gross_salary_no_longer_crashes() {
    var paySlip = invoiceService.generatePaySlip(payslipWorker(), YearMonth.of(2026, 5));

    assertBigDecimalEquals(-450000, paySlip.taxableGrossAmount());
    assertBigDecimalEquals(0, paySlip.employeeTotalTaxAmount());
    assertBigDecimalEquals(3000, paySlip.deductionTotalAmount());
    assertBigDecimalEquals(-453000, paySlip.netAmount());
  }

  @Test
  void a_salary_spanning_every_bracket_is_taxed_slice_by_slice() {
    var worker = new Worker("W-PAYSLIP-02", "Hery Randria", "", "", "", "", "", "", null, null);

    var paySlip = invoiceService.generatePaySlip(worker, YearMonth.of(2026, 3));

    assertBigDecimalEquals(new BigDecimal("1412612.26"), paySlip.taxableGrossAmount());
    assertBigDecimalEquals(new BigDecimal("28252.24"), paySlip.employeeTotalTaxAmount());
    assertBigDecimalEquals(new BigDecimal("1384360.02"), paySlip.amountAfterTaxes());
    assertBigDecimalEquals(new BigDecimal("184360.00"), paySlip.deductionTotalAmount());
    assertBigDecimalEquals(new BigDecimal("1200000.02"), paySlip.netAmount());
  }

  @Test
  void the_reduction_for_dependents_is_capped_at_six() {
    var worker = new Worker("W-PAYSLIP-03", "Zo Rakotoarisoa", "", "", "", "", "", "", 8, null);

    var paySlip = invoiceService.generatePaySlip(worker, YearMonth.of(2026, 3));

    assertBigDecimalEquals(new BigDecimal("12000"), paySlip.reductionForDependents());
    assertBigDecimalEquals(new BigDecimal("3410.00"), paySlip.deductionTotalAmount());
    assertBigDecimalEquals(new BigDecimal("515990.00"), paySlip.netAmount());
  }

  private void assertBigDecimalEquals(long expected, BigDecimal actual) {
    assertBigDecimalEquals(BigDecimal.valueOf(expected), actual);
  }

  private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
    assertEquals(
        0, expected.compareTo(actual), () -> "expected " + expected + " but was " + actual);
  }
}
