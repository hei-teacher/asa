package school.hei.asa.endpoint.rest.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.Month;
import java.time.YearMonth;
import java.util.Base64;
import java.util.EnumSet;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThInvoiceFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThInvoice;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.endpoint.rest.model.th.ThMonthInvoiceStatus;
import school.hei.asa.model.Worker;
import school.hei.asa.service.InvoicePDFGenerator;
import school.hei.asa.service.InvoiceService;

@Slf4j
@AllArgsConstructor
@Service
public class ThInvoiceService {
  private final InvoiceService invoiceService;
  private final ThInvoiceFormMapper thInvoiceFormMapper;
  private final InvoicePDFGenerator invoicePDFGenerator;

  public String generateInvoiceFileName(Worker worker) {
    return invoiceService.generateInvoiceFileName(worker);
  }

  public void saveInvoiceReference(ThInvoiceForm thInvoiceForm, Worker worker) {
    var invoiceData = thInvoiceFormMapper.toDomain(thInvoiceForm);
    invoiceService.saveInvoiceReference(invoiceData, worker);
  }

  public List<ThMonthInvoiceStatus> getMonthInvoiceStatusForWorker(Worker worker, int year) {
    EnumSet<Month> months = EnumSet.allOf(Month.class);
    return months.stream()
        .map(
            month -> {
              var yearMonth = YearMonth.of(year, month.getValue());
              var invoiceReference = invoiceService.findInvoiceReference(worker, yearMonth);
              return new ThMonthInvoiceStatus(yearMonth, invoiceReference.isPresent());
            })
        .toList();
  }

  @SneakyThrows
  public ThInvoice extractInvoice(Worker worker, ThInvoiceForm invoiceForm) {
    var invoiceData =
        invoiceService.extractInvoiceData(worker, thInvoiceFormMapper.toDomain(invoiceForm));
    log.info("mapping invoice to th ...");
    var thInvoiceData = thInvoiceFormMapper.toTh(invoiceData);
    log.info("successfully mapped to th");
    File data = invoicePDFGenerator.apply(worker, thInvoiceData, "invoice");

    try (PDDocument document = PDDocument.load(data)) {
      PDFRenderer pdfRenderer = new PDFRenderer(document);
      BufferedImage image = pdfRenderer.renderImageWithDPI(0, 150);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "png", baos);
      String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

      log.info("successfully extracted invoiceData");
      return new ThInvoice(base64Image, thInvoiceData);
    }
  }
}
