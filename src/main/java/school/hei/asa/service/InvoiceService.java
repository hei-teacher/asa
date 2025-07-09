package school.hei.asa.service;

import static java.time.LocalDate.now;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.format.TextStyle.FULL;
import static java.util.Locale.FRENCH;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.Invoice;
import school.hei.asa.model.Worker;
import school.hei.asa.service.utils.NumberConverter;
import school.hei.asa.service.utils.NumberParser;

@AllArgsConstructor
@Service
public class InvoiceService {
  private final InvoicePDFGenerator invoicePDFGenerator;
  private final NumberConverter numberConverter;
  private final NumberParser numberParser;

  @SneakyThrows
  public Invoice extractInvoice(Worker worker, ThInvoiceForm invoiceForm) {
    var invoiceData = extractInvoiceData(invoiceForm);
    File data = invoicePDFGenerator.apply(worker, invoiceData, "invoice");

    try (PDDocument document = PDDocument.load(data)) {
      PDFRenderer pdfRenderer = new PDFRenderer(document);
      BufferedImage image = pdfRenderer.renderImageWithDPI(0, 150);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "png", baos);
      String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

      return new Invoice(base64Image, invoiceData);
    }
  }

  public String generateInvoiceFileName(String invoiceIssueDate, String workerName) {
    var invoiceDate = parse(invoiceIssueDate, ofPattern("dd/MM/yyyy", FRENCH));
    String month = invoiceDate.getMonth().getDisplayName(FULL, FRENCH);
    String capitalizedMonth = month.substring(0, 1).toUpperCase() + month.substring(1);
    return workerName + " - " + capitalizedMonth + ".pdf";
  }

  private ThInvoiceForm extractInvoiceData(ThInvoiceForm invoiceForm) {
    var formatter = ofPattern("dd/MM/yyyy", FRENCH);
    var isEmpty = invoiceForm.issueDate() == null || invoiceForm.issueDate().isBlank();
    var today = now();
    var firstDay = today.withDayOfYear(1);
    var reference = isEmpty ? "FAC00/00/0000" : "FAC" + today.format(formatter);
    var issueDate = isEmpty ? firstDay.format(formatter) : invoiceForm.issueDate();
    var unitPrice =
        isEmpty
            ? ""
            : numberParser.parseToNumber(numberParser.parseToDouble(invoiceForm.unitPrice()));
    var doubleAmount =
        isEmpty
            ? 0.0
            : numberParser.parseToDouble(invoiceForm.quantity())
                * numberParser.parseToDouble(invoiceForm.unitPrice());
    var amount = numberParser.parseToNumber(doubleAmount);
    var hasBonus = !isEmpty && invoiceForm.hasBonus();
    var bonusUnitPrice =
        !hasBonus
            ? ""
            : numberParser.parseToNumber(numberParser.parseToDouble(invoiceForm.bonusUnitPrice()));
    var doubleBonusAmount =
        !hasBonus
            ? 0.0
            : numberParser.parseToDouble(invoiceForm.bonusQuantity())
                * numberParser.parseToDouble(invoiceForm.bonusUnitPrice());
    var bonusAmount = numberParser.parseToNumber(doubleBonusAmount);
    var total = numberParser.parseToNumber(doubleAmount + doubleBonusAmount);
    var parsedAmount = isEmpty ? "" : numberConverter.convertToWords(total);

    return new ThInvoiceForm(
        reference,
        issueDate,
        invoiceForm.description(),
        invoiceForm.quantity(),
        unitPrice,
        amount,
        total,
        hasBonus,
        invoiceForm.bonusDescription(),
        invoiceForm.bonusQuantity(),
        bonusUnitPrice,
        bonusAmount,
        parsedAmount);
  }
}
