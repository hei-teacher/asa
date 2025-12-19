package school.hei.asa.endpoint.rest.controller;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_PDF;

import java.io.File;
import java.io.FileInputStream;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.ThInvoiceService;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.service.InvoicePDFGenerator;
import school.hei.asa.service.InvoiceService;

@Slf4j
@AllArgsConstructor
@Controller
public class InvoiceController {

  private final WorkerFromAuthentication workerFromAuthentication;
  private final WorkerToModelAdder workerToModelAdder;
  private final InvoicePDFGenerator invoicePDFGenerator;
  private final InvoiceService invoiceService;
  private final BucketComponent bucketComponent;
  private final ThInvoiceService thInvoiceService;
  private static final String INVOICES_FOLDER = "invoices/";

  @GetMapping("/invoice")
  public String getInvoicePage(
      Model model, Authentication authentication, @ModelAttribute ThInvoiceForm invoiceForm) {
    var pattern = DateTimeFormatter.ofPattern("yyyy-MM");
    var workerCodeOrAuth = workerFromAuthentication.apply(authentication).get().code();
    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    var yearMonth =
        invoiceForm.yearMonth() != null
            ? YearMonth.parse(invoiceForm.yearMonth(), pattern)
            : YearMonth.now();
    var monthInvoiceStatus = thInvoiceService.getMonthInvoiceStatusForWorker(worker);
    var invoiceReference = invoiceService.findInvoiceReference(worker, yearMonth);

    model.addAttribute("yearMonthReference", invoiceForm.yearMonth());
    model.addAttribute("invoiceReference", invoiceReference);
    model.addAttribute("monthInvoiceStatuses", monthInvoiceStatus);

    return "invoice-generator";
  }

  @GetMapping("/invoice/preview")
  public ResponseEntity<Resource> previewInvoice(
      Model model, Authentication authentication, @ModelAttribute ThInvoiceForm invoiceForm) {
    var workerCodeOrAuth = workerFromAuthentication.apply(authentication).get().code();
    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    var invoice = thInvoiceService.extractInvoice(worker, invoiceForm);

    File pdfFile = invoicePDFGenerator.apply(worker, invoice.invoiceData(), "invoice");
    FileSystemResource resource = new FileSystemResource(pdfFile);
    return ResponseEntity.ok()
        .contentType(APPLICATION_PDF)
        .header(CONTENT_DISPOSITION, "inline; filename=invoice-preview")
        .body(resource);
  }

  @SneakyThrows
  @GetMapping("/invoice/generate")
  public ResponseEntity<byte[]> downloadInvoicePDF(
      Model model, Authentication authentication, @ModelAttribute ThInvoiceForm invoiceForm) {
    var workerCodeOrAuth = workerFromAuthentication.apply(authentication).get().code();
    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    var invoice = thInvoiceService.extractInvoice(worker, invoiceForm);

    File pdfFile = invoicePDFGenerator.apply(worker, invoice.invoiceData(), "invoice");
    var fileBytes = new FileInputStream(pdfFile).readAllBytes();
    log.info("saving reference to database...");
    thInvoiceService.saveInvoiceReference(invoice.invoiceData(), worker);
    log.info("Generating name for bucket key...");
    var fileName = thInvoiceService.generateInvoiceFileName(worker);
    log.info("uploading...");
    bucketComponent.upload(pdfFile, INVOICES_FOLDER + fileName);

    return ResponseEntity.ok()
        .header(CONTENT_DISPOSITION, "attachment; filename=" + fileName)
        .contentType(APPLICATION_PDF)
        .body(fileBytes);
  }
}
