package school.hei.asa.service;

import com.lowagie.text.DocumentException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.file.FileWriter;
import school.hei.asa.model.Worker;

@Component
@AllArgsConstructor
public class InvoicePDFGenerator {
  private final FileWriter fileWriter;
  private final TemplateResolverEngine templateResolverEngine;

  public File apply(Worker worker, ThInvoiceForm thInvoiceForm, String template) {
    ITextRenderer renderer = new ITextRenderer();
    loadStyle(renderer, worker, thInvoiceForm, template);
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
      ITextRenderer renderer, Worker worker, ThInvoiceForm thInvoiceForm, String template) {
    renderer.setDocumentFromString(parseInvoiceTemplateToString(worker, thInvoiceForm, template));
  }

  private String parseInvoiceTemplateToString(
      Worker worker, ThInvoiceForm thInvoiceForm, String template) {
    TemplateEngine templateEngine = templateResolverEngine.getTemplateEngine();
    Context context = configureContext(worker, thInvoiceForm);
    return templateEngine.process(template, context);
  }

  private Context configureContext(Worker worker, ThInvoiceForm thInvoiceForm) {
    Context context = new Context();
    context.setVariable("worker", worker);
    context.setVariable("invoice", thInvoiceForm);

    return context;
  }
}
