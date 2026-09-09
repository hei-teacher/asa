package school.hei.asa.endpoint.rest.service;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.FRENCH;

import com.lowagie.text.DocumentException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
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
import school.hei.asa.number.NumberParser;
import school.hei.asa.service.TemplateResolverEngine;

@Component
@AllArgsConstructor
public class InvoicePDFGenerator {
  // ponytail: rates mirror migration V101_4 seed data; replace with a real Tax/Credit lookup
  // once that repository plumbing lands.
  private static final double CNAPS_EMPLOYEE_RATE = 0.01;
  private static final double CNAPS_EMPLOYER_RATE = 0.13;
  private static final double OSTIE_EMPLOYEE_RATE = 0.01;
  private static final double OSTIE_EMPLOYER_RATE = 0.05;
  private static final double FMFP_EMPLOYER_RATE = 0.01;

  private final FileWriter fileWriter;
  private final TemplateResolverEngine templateResolverEngine;
  private final NumberParser numberParser;

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
    context.setVariable("invoiceId", thInvoiceForm.id());
    context.setVariable("invoice", thInvoiceForm);
    context.setVariable("yearMonth", String.format("%s %s", month, year));

    var base = numberParser.parseToDouble(thInvoiceForm.amount());
    context.setVariable("cnapsEmployee", formatTax(base, CNAPS_EMPLOYEE_RATE));
    context.setVariable("cnapsEmployer", formatTax(base, CNAPS_EMPLOYER_RATE));
    context.setVariable("ostieEmployee", formatTax(base, OSTIE_EMPLOYEE_RATE));
    context.setVariable("ostieEmployer", formatTax(base, OSTIE_EMPLOYER_RATE));
    context.setVariable("fmfpEmployer", formatTax(base, FMFP_EMPLOYER_RATE));
    context.setVariable(
        "totalCotisationEmployee", formatTax(base, CNAPS_EMPLOYEE_RATE + OSTIE_EMPLOYEE_RATE));
    context.setVariable(
        "totalCotisationEmployer",
        formatTax(base, CNAPS_EMPLOYER_RATE + OSTIE_EMPLOYER_RATE + FMFP_EMPLOYER_RATE));

    return context;
  }

  private String formatTax(double base, double rate) {
    return numberParser.parseToNumber(BigDecimal.valueOf(base * rate));
  }
}
