package school.hei.asa.service;

import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
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

  public File apply(ThInvoiceForm thInvoiceForm, String template) {
    ITextRenderer renderer = new ITextRenderer();
    loadStyle(renderer, thInvoiceForm,template);
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
      ThInvoiceForm thInvoiceForm,
      String template) {
    renderer.setDocumentFromString(
        parseInvoiceTemplateToString(thInvoiceForm, template));
  }

  private String parseInvoiceTemplateToString(ThInvoiceForm thInvoiceForm, String template) {
    TemplateEngine templateEngine = templateResolverEngine.getTemplateEngine();
    Context context = configureContext(thInvoiceForm);
    return templateEngine.process(template, context);
  }

  private Context configureContext(ThInvoiceForm thInvoiceForm) {
    Context context = new Context();
    context.setVariable("invoice", thInvoiceForm);

    return context;
  }
}
