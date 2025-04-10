package school.hei.asa.service;

import school.hei.asa.file.FileWriter;
import com.lowagie.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Component
@AllArgsConstructor
public class InvoicePDFGenerator {
  private final FileWriter fileWriter;
  private final TemplateResolverEngine templateResolverEngine;

  public File apply(/*Invoice invoice, AccountHolder accountHolder, File logoFile, */String template) {
    ITextRenderer renderer = new ITextRenderer();
    loadStyle(renderer, /*invoice, accountHolder, logoFile, */template);
    renderer.layout();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      renderer.createPDF(outputStream);
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }
    return fileWriter.apply(outputStream.toByteArray(), null);
  }

  private void loadStyle(
      ITextRenderer renderer,
      /*Invoice invoice,
      AccountHolder accountHolder,
      File logoFile,*/
      String template) {
    renderer.setDocumentFromString(
        parseInvoiceTemplateToString(/*invoice, accountHolder, logoFile, */template));
  }

  private String parseInvoiceTemplateToString(
      /*Invoice invoice, AccountHolder accountHolder, File logoFile, */String template) {
    TemplateEngine templateEngine = templateResolverEngine.getTemplateEngine();
    Context context = configureContext(/*invoice, accountHolder, logoFile*/);
    return templateEngine.process(template, context);
  }

  private Context configureContext(/*Invoice invoice, AccountHolder accountHolder, File logoFile*/) {
    Context context = new Context();
    /*Account account = invoice.getActualAccount();*/
    //byte[] logoAsBytes = fileWriter.writeAsByte(logoFile);

    // context.setVariable("invoice", invoice);
    //context.setVariable("logo", base64Image(logoAsBytes));
    // context.setVariable("account", account);
    // context.setVariable("accountHolder", accountHolder);
    return context;
  }
}
