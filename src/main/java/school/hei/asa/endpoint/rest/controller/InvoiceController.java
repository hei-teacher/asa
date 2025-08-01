package school.hei.asa.endpoint.rest.controller;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_PDF;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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
import school.hei.asa.service.InvoiceService;

@AllArgsConstructor
@Controller
public class InvoiceController {

  private final WorkerFromAuthentication workerFromAuthentication;
  private final WorkerToModelAdder workerToModelAdder;
  private final InvoicePDFGenerator invoicePDFGenerator;
  private final InvoiceService invoiceService;

  @SneakyThrows
  @GetMapping("/invoice")
  public String getInvoicePage(
      Model model, Authentication authentication, @ModelAttribute ThInvoiceForm invoiceForm) {
    var workerCodeOrAuth = workerFromAuthentication.apply(authentication).get().code();
    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    var invoice = invoiceService.extractInvoice(worker, invoiceForm);

    File pdfFile = invoicePDFGenerator.apply(worker, invoice.invoiceData(), "invoice");
    var fileUri = pdfFile.toURI().toString();

    model.addAttribute("invoicePreview", fileUri);
    model.addAttribute("form", invoice.invoiceData());

    return "invoice-generator";
  }

  @SneakyThrows
  @GetMapping("/invoice/preview")
  public ResponseEntity<Resource> previewInvoice(Model model, Authentication authentication, @ModelAttribute ThInvoiceForm invoiceForm) {
    var workerCodeOrAuth = workerFromAuthentication.apply(authentication).get().code();
    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    var invoice = invoiceService.extractInvoice(worker, invoiceForm);

    File pdfFile = invoicePDFGenerator.apply(worker, invoice.invoiceData(), "invoice");
    Path path = pdfFile.toPath();
    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf")
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
    var fileName = invoiceService.generateInvoiceFileName(invoiceForm.issueDate(), worker.name());

    return ResponseEntity.ok()
        .header(CONTENT_DISPOSITION, "attachment; filename=" + fileName)
        .contentType(APPLICATION_PDF)
        .body(fileBytes);
  }
}
