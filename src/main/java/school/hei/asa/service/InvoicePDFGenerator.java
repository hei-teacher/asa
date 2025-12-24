package school.hei.asa.service;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.FRENCH;

import com.lowagie.text.DocumentException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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
    var renderer = new ITextRenderer();
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
    var templateEngine = templateResolverEngine.getTemplateEngine();
    var context = configureContext(worker, thInvoiceForm);
    return templateEngine.process(template, context);
  }

  private Context configureContext(Worker worker, ThInvoiceForm thInvoiceForm) {
    var pattern = DateTimeFormatter.ofPattern("yyyy-MM");
    var date = YearMonth.parse(thInvoiceForm.yearMonth(), pattern);
    var month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE).toLowerCase();
    var year = date.getYear();
    Context context = new Context();
    context.setVariable("creationDate", now().format(ofPattern("dd LLLL yyyy à HH:mm:ss", FRENCH)));
    context.setVariable("worker", worker);
    context.setVariable("invoice", thInvoiceForm);
    context.setVariable("yearMonth", String.format("%s %s", month, year));

    return context;
  }
}
