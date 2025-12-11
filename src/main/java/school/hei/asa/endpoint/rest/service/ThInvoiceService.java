package school.hei.asa.endpoint.rest.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
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

      return new ThInvoice(base64Image, thInvoiceData);
    }
  }

  public String generateInvoiceFileName(String yearMonth, String workerCode) {
    return String.format("FAC-NUMERMG-%s-%s.pdf", workerCode, yearMonth);
  }
}
