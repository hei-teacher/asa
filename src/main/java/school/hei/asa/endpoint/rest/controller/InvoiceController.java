package school.hei.asa.endpoint.rest.controller;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_PDF;

import java.io.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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
import school.hei.asa.service.InvoicePDFGenerator;
import school.hei.asa.service.InvoiceService;

@AllArgsConstructor
@Controller
public class InvoiceController {

  private final WorkerFromAuthentication workerFromAuthentication;
  private final WorkerToModelAdder workerToModelAdder;
  private final InvoicePDFGenerator invoicePDFGenerator;
  private final InvoiceService invoiceService;

  @GetMapping("/invoice")
  public String getInvoicePage(Model model, @ModelAttribute ThInvoiceForm invoiceForm) {
    model.addAttribute("dateReference", invoiceForm.reference());

    return "invoice-generator";
  }

  @GetMapping("/invoice/preview")
  public ResponseEntity<Resource> previewInvoice(
      Model model, Authentication authentication, @ModelAttribute ThInvoiceForm invoiceForm) {
    var workerCodeOrAuth = workerFromAuthentication.apply(authentication).get().code();
    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    var invoice = invoiceService.extractInvoice(worker, invoiceForm);
    var timestamp = now().format(ofPattern("yyyyMMdd-HHmmss"));
    String fileName = "invoice-" + worker.code() + "-" + timestamp + ".pdf";

    File pdfFile = invoicePDFGenerator.apply(worker, invoice.invoiceData(), "invoice");
    FileSystemResource resource = new FileSystemResource(pdfFile);

    return ResponseEntity.ok()
        .contentType(APPLICATION_PDF)
        .header(CONTENT_DISPOSITION, "inline; filename=" + fileName)
        .body(resource);
  }

  @SneakyThrows
  @GetMapping("/invoice/download")
  public ResponseEntity<byte[]> downloadInvoicePDF(
      Model model, Authentication authentication, @ModelAttribute ThInvoiceForm invoiceForm) {
    var workerCodeOrAuth = workerFromAuthentication.apply(authentication).get().code();
    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    var invoice = invoiceService.extractInvoice(worker, invoiceForm);

    File pdfFile = invoicePDFGenerator.apply(worker, invoice.invoiceData(), "invoice");
    var fileBytes = new FileInputStream(pdfFile).readAllBytes();
    var fileName = invoiceService.generateInvoiceFileName(invoiceForm.reference(), worker.name());

    return ResponseEntity.ok()
        .header(CONTENT_DISPOSITION, "attachment; filename=" + fileName)
        .contentType(APPLICATION_PDF)
        .body(fileBytes);
  }
}
