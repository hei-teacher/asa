package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.PaySlipRepository;

class PaySlipDocumentStatusIT extends FacadeIT {
  private static final YearMonth GENERATED_MONTH = YearMonth.of(2026, 7);

  @Autowired InvoiceService invoiceService;
  @Autowired PaySlipRepository paySlipRepository;

  private Worker payslipWorker() {
    return new Worker("W-PAYSLIP-01", "Rina Rakoto", "", "", "", "", "", "", null, null);
  }

  private void generateAndSavePaySlip() {
    var worker = payslipWorker();
    paySlipRepository.save(invoiceService.generatePaySlip(worker, GENERATED_MONTH), worker);
  }

  @Test
  void a_month_without_any_document_is_not_marked_as_generated() {
    assertFalse(invoiceService.hasGeneratedDocument(payslipWorker(), YearMonth.of(2020, 1)));
  }

  @Test
  void a_saved_payslip_marks_the_month_as_generated() {
    generateAndSavePaySlip();

    assertTrue(invoiceService.hasGeneratedDocument(payslipWorker(), GENERATED_MONTH));
  }

  @Test
  void a_saved_payslip_resolves_to_the_payslips_bucket_folder() {
    generateAndSavePaySlip();

    var bucketKey = invoiceService.resolveBucketKey(payslipWorker(), GENERATED_MONTH);

    assertEquals("payslips/FDP_Numer_2026_W-PAYSLIP-01_2026-07.pdf", bucketKey);
  }
}
