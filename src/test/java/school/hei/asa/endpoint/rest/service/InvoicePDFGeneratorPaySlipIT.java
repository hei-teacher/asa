package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.nio.file.Files;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.Worker;
import school.hei.asa.number.NumberParser;

// Rien ne rend jamais reellement le template pay-slip.html de bout en bout ailleurs : les tests
// existants (generatePaySlip) ne verifient que le calcul, jamais que le HTML se rend sans erreur
// avec les vraies variables de contexte.
class InvoicePDFGeneratorPaySlipIT extends FacadeIT {
  @Autowired InvoicePDFGenerator invoicePDFGenerator;
  @Autowired NumberParser numberParser;

  private Worker payslipWorker() {
    return new Worker("W-PAYSLIP-01", "Rina Rakoto", "", "", "", "", "", "", null, null);
  }

  private ThInvoiceForm paySlipForm(String yearMonth) {
    return new ThInvoiceForm(
        null, yearMonth, null, null, "", "", "", "", false, "", "", "", "", "", "", "");
  }

  @Test
  void renders_a_valid_non_empty_pdf_for_a_full_time_employee() throws Exception {
    var file = invoicePDFGenerator.apply(payslipWorker(), paySlipForm("2026-03"), "pay-slip");

    assertTrue(file.exists());
    assertTrue(file.length() > 0);
    var header = new String(Files.readAllBytes(file.toPath()), 0, 4);
    assertTrue(header.equals("%PDF"), () -> "expected a PDF file, got header: " + header);
  }

  @Test
  void rendered_pdf_shows_the_net_amount_computed_by_the_service() throws Exception {
    var file = invoicePDFGenerator.apply(payslipWorker(), paySlipForm("2026-03"), "pay-slip");

    // 450 000 - 9 000 (CNAPS/OSTIE) - 44 100 (IRSA) = 396 900, cf. PaySlipGenerationIT
    var expectedNetAmount = numberParser.parseToNumber(BigDecimal.valueOf(396900));

    try (var document = PDDocument.load(file)) {
      var text = new PDFTextStripper().getText(document);
      assertTrue(
          text.contains(expectedNetAmount),
          () -> "expected the rendered PDF to contain " + expectedNetAmount);
    }
  }
}
