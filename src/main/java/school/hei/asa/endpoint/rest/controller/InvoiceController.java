package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.service.InvoicePDFGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

@AllArgsConstructor
@Controller
public class InvoiceController {

  private final InvoicePDFGenerator invoicePDFGenerator;

  @SneakyThrows
  @GetMapping("/invoice")
  public String getInvoicePage(Model model, @ModelAttribute ThInvoiceForm invoiceForm){

    var isEmpty = invoiceForm.reference() == null || invoiceForm.reference().isBlank();
    var invoiceData = isEmpty ? new ThInvoiceForm("FAC00/00/0000", "00/00/0000", "", "0", "0 Ar", "0 Ar", "0 Ar", false, "", "", "", "") : invoiceForm;

    File data = invoicePDFGenerator.apply(invoiceData, "invoice");
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
}
