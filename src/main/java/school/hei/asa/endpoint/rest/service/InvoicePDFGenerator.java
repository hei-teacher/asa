package school.hei.asa.endpoint.rest.service;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.FRENCH;

import com.lowagie.text.DocumentException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.file.FileWriter;
import school.hei.asa.model.DeductionType;
import school.hei.asa.model.EarnedCredit;
import school.hei.asa.model.Worker;
import school.hei.asa.number.NumberParser;
import school.hei.asa.repository.CreditRepository;
import school.hei.asa.service.InvoiceService;
import school.hei.asa.service.TemplateResolverEngine;

@Component
@AllArgsConstructor
public class InvoicePDFGenerator {
  private static final String PAY_SLIP_TEMPLATE = "pay-slip";

  private final FileWriter fileWriter;
  private final TemplateResolverEngine templateResolverEngine;
  private final NumberParser numberParser;
  private final InvoiceService invoiceService;
  private final CreditRepository creditRepository;

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
    var context = configureContext(worker, thInvoiceForm, template);
    return templateEngine.process(template, context);
  }

  private Context configureContext(Worker worker, ThInvoiceForm thInvoiceForm, String template) {
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

    if (PAY_SLIP_TEMPLATE.equals(template)) {
      configurePaySlipContext(context, worker, date);
    }

    return context;
  }

  private void configurePaySlipContext(Context context, Worker worker, YearMonth yearMonth) {
    var paySlip = invoiceService.generatePaySlip(worker, yearMonth);
    var grossAmount = paySlip.grossAmount();
    var taxableBase = paySlip.taxableGrossAmount();

    context.setVariable("payslipGrossAmount", format(grossAmount));
    context.setVariable("payslipTaxableGrossAmount", format(taxableBase));
    context.setVariable("payslipNetAmount", format(paySlip.netAmount()));
    context.setVariable("payslipEmployeeTotalTaxAmount", format(paySlip.employeeTotalTaxAmount()));
    context.setVariable("payslipEmployerTotalTaxAmount", format(paySlip.employerTotalTaxAmount()));
    context.setVariable("paidLeave", paySlip.paidLeave());

    Map<String, String> taxEmployeeAmounts = new HashMap<>();
    Map<String, String> taxEmployerAmounts = new HashMap<>();
    var socialContributionEmployee = BigDecimal.ZERO;
    var socialContributionEmployer = BigDecimal.ZERO;
    for (var tax : paySlip.taxes()) {
      var amount = tax.resolve(taxableBase);
      taxEmployeeAmounts.put(tax.getId(), format(amount.employeeContributionValue()));
      taxEmployerAmounts.put(tax.getId(), format(amount.employerContributionValue()));
      if (tax.getDeductionType() == DeductionType.TAX) {
        socialContributionEmployee =
            socialContributionEmployee.add(amount.employeeContributionValue());
        socialContributionEmployer =
            socialContributionEmployer.add(amount.employerContributionValue());
      }
    }
    context.setVariable("taxEmployeeAmounts", taxEmployeeAmounts);
    context.setVariable("taxEmployerAmounts", taxEmployerAmounts);
    context.setVariable("totalCotisationEmployee", format(socialContributionEmployee));
    context.setVariable("totalCotisationEmployer", format(socialContributionEmployer));

    var earnedByCode = new HashMap<String, EarnedCredit>();
    for (var earnedCredit : paySlip.credits()) {
      earnedByCode.put(earnedCredit.getCredit().getCreditCode(), earnedCredit);
    }

    Map<String, String> creditBases = new HashMap<>();
    Map<String, String> creditAmounts = new HashMap<>();
    Map<String, Double> creditQuantities = new HashMap<>();
    for (var credit : creditRepository.findAll()) {
      var base =
          credit.getDivisor() == null
              ? grossAmount
              : grossAmount.divide(
                  BigDecimal.valueOf(credit.getDivisor()), 2, RoundingMode.HALF_UP);
      creditBases.put(credit.getCreditCode(), format(base));
      var earned = earnedByCode.get(credit.getCreditCode());
      if (earned != null) {
        creditAmounts.put(credit.getCreditCode(), format(earned.getAmount(grossAmount)));
        creditQuantities.put(credit.getCreditCode(), earned.getNumberOfUnits());
      }
    }
    context.setVariable("creditBases", creditBases);
    context.setVariable("creditAmounts", creditAmounts);
    context.setVariable("creditQuantities", creditQuantities);
  }

  private String format(BigDecimal amount) {
    return numberParser.parseToNumber(amount);
  }
}
