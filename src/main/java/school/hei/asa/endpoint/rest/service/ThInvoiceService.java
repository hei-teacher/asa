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
import school.hei.asa.model.contract.ContractType;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.InvoiceService;

@Slf4j
@AllArgsConstructor
@Service
public class ThInvoiceService {
  private static final String INVOICE_TEMPLATE = "invoice";
  private static final String PAY_SLIP_TEMPLATE = "pay-slip";

  private final InvoiceService invoiceService;
  private final ThInvoiceFormMapper thInvoiceFormMapper;
  private final InvoicePDFGenerator invoicePDFGenerator;
  private final ContractService contractService;

  public String generateInvoiceFileName(Worker worker) {
    return invoiceService.generateInvoiceFileName(worker);
  }

  public void saveInvoice(ThInvoiceForm thInvoiceForm, Worker worker) {
    var invoiceData = thInvoiceFormMapper.toDomain(thInvoiceForm);
    invoiceService.saveInvoice(invoiceData, worker);
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

  public String resolveTemplateName(Worker worker) {
    return contractService
        .findActiveContractByWorker(worker)
        .map(contract -> contract.level().type())
        .filter(ContractType.fullTimeEmployee::equals)
        .map(type -> PAY_SLIP_TEMPLATE)
        .orElse(INVOICE_TEMPLATE);
  }

  @SneakyThrows
  public ThInvoice extractInvoice(Worker worker, ThInvoiceForm invoiceForm) {
    var invoiceData =
        invoiceService.extractInvoiceForm(worker, thInvoiceFormMapper.toDomain(invoiceForm));
    log.info("mapping invoice to th ...");
    var thInvoiceData = thInvoiceFormMapper.toTh(invoiceData);
    log.info("successfully mapped to th");
    File data = invoicePDFGenerator.apply(worker, thInvoiceData, resolveTemplateName(worker));

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
