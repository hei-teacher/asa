package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.service.InvoicePDFGenerator;
import school.hei.asa.service.utils.ToWords;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.format.TextStyle.FULL;
import static java.util.Locale.FRENCH;

@AllArgsConstructor
@Controller
public class InvoiceController {

  private final WorkerFromAuthentication workerFromAuthentication;
  private final WorkerToModelAdder workerToModelAdder;
  private final InvoicePDFGenerator invoicePDFGenerator;

  @SneakyThrows
  @GetMapping("/invoice")
  public String getInvoicePage(Model model, Authentication authentication, @ModelAttribute ThInvoiceForm invoiceForm){
    var workerCodeOrAuth = workerFromAuthentication.apply(authentication).get().code();
    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    var invoiceData = extractInvoiceData(invoiceForm);
    File data = invoicePDFGenerator.apply(worker, invoiceData, "invoice");
    BufferedImage image;

    try (PDDocument document = PDDocument.load(new File(String.valueOf(data.toPath())))) {
      PDFRenderer pdfRenderer = new PDFRenderer(document);
      image = pdfRenderer.renderImageWithDPI(0, 150);
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, "png", baos);
    String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

    model.addAttribute("invoiceData", base64Image);
    model.addAttribute("form", invoiceData);

    return "invoice-generator";
  }

  @SneakyThrows
  @GetMapping("/invoice/download")
  public ResponseEntity<byte[]> downloadInvoicePDF(Model model, Authentication authentication, @ModelAttribute ThInvoiceForm invoiceForm){
    var workerCodeOrAuth = workerFromAuthentication.apply(authentication).get().code();
    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    var invoiceData = extractInvoiceData(invoiceForm);
    File pdfFile = invoicePDFGenerator.apply(worker, invoiceData, "invoice");

    var invoiceDate = parse(invoiceData.issueDate(), ofPattern("dd/MM/yyyy", FRENCH));
    String month = invoiceDate.getMonth().getDisplayName(FULL, FRENCH);
    String capitalizedMonth = month.substring(0, 1).toUpperCase() + month.substring(1);
    var fileName = worker.name() + " - " + capitalizedMonth + ".pdf";
    var fileBytes = new FileInputStream(pdfFile).readAllBytes();

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
            .contentType(MediaType.APPLICATION_PDF)
            .body(fileBytes);
  }

  private static ThInvoiceForm extractInvoiceData(ThInvoiceForm invoiceForm) {
    var toWords = new ToWords();
    var isEmpty = invoiceForm.reference() == null || invoiceForm.reference().isBlank();
    var reference = isEmpty ? "FAC00/00/0000" : invoiceForm.reference();
    var issueDate = isEmpty ? "01/01/2025" : invoiceForm.issueDate();
    var amount = isEmpty ? "0 Ar" : invoiceForm.amount();
    var total = isEmpty ? "0 Ar" : invoiceForm.total();
    var hasBonus = !isEmpty && invoiceForm.hasBonus();
    var parsedAmount = isEmpty ? "" : toWords.convertToWords(invoiceForm.total());

      return new ThInvoiceForm(reference, issueDate, invoiceForm.description(), invoiceForm.quantity(), invoiceForm.unitPrice(), amount, total, hasBonus, invoiceForm.bonusDescription(), invoiceForm.bonusQuantity(), invoiceForm.bonusUnitPrice(), invoiceForm.bonusAmount(), parsedAmount);
  }
}
