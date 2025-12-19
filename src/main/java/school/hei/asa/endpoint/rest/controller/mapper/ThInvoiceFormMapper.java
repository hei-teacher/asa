package school.hei.asa.endpoint.rest.controller.mapper;

import static java.lang.Double.parseDouble;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.FRENCH;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.service.utils.NumberParser;

@Slf4j
@AllArgsConstructor
@Component
public class ThInvoiceFormMapper {
  private final NumberParser numberParser;
  private final DateTimeFormatter yearMonthFormatter = ofPattern("yyyy-MM");
  private final DateTimeFormatter localDateFormatter = ofPattern("dd/MM/yyyy", FRENCH);

  public ThInvoiceForm toTh(InvoiceForm invoiceForm) {
    return new ThInvoiceForm(
        invoiceForm.yearMonth().format(yearMonthFormatter),
        invoiceForm.referenceDate().format(localDateFormatter),
        invoiceForm.issueDate().format(localDateFormatter),
        invoiceForm.description(),
        String.valueOf(invoiceForm.quantity()),
        numberParser.parseToNumber(invoiceForm.unitPrice()),
        numberParser.parseToNumber(invoiceForm.amount()),
        invoiceForm.hasUpgradedLevel(),
        invoiceForm.extraDescription(),
        String.valueOf(invoiceForm.extraQuantity()),
        numberParser.parseToNumber(invoiceForm.extraUnitPrice()),
        numberParser.parseToNumber(invoiceForm.extraAmount()),
        numberParser.parseToNumber(invoiceForm.total()),
        invoiceForm.parsedAmount(),
        invoiceForm.rib());
  }

  public InvoiceForm toDomain(ThInvoiceForm invoiceForm) {
    log.info("mapping ThInvoiceForm {}", invoiceForm);
    var yearMonth =
        invoiceForm.yearMonth() != null && !invoiceForm.yearMonth().isBlank()
            ? YearMonth.parse(invoiceForm.yearMonth(), yearMonthFormatter)
            : null;
    log.info("1");
    var reference =
        invoiceForm.reference() != null && !invoiceForm.reference().isBlank()
            ? LocalDate.parse(invoiceForm.reference(), localDateFormatter)
            : null;
    var issueDate =
        invoiceForm.issueDate() != null && !invoiceForm.issueDate().isBlank()
            ? LocalDate.parse(invoiceForm.issueDate(), localDateFormatter)
            : null;
    var quantity =
        invoiceForm.quantity() != null && !invoiceForm.quantity().isBlank()
            ? parseDouble(invoiceForm.quantity())
            : null;
    var unitPrice =
        invoiceForm.unitPrice() != null && !invoiceForm.unitPrice().isBlank()
            ? new BigDecimal(invoiceForm.unitPrice().replace(" ", ""))
            : null;
    var amount =
        invoiceForm.amount() != null && !invoiceForm.amount().isBlank()
            ? new BigDecimal(invoiceForm.amount().replace(" ", ""))
            : null;
    var extraQuantity =
        invoiceForm.extraQuantity() != null
                && !invoiceForm.extraQuantity().isBlank()
                && !invoiceForm.extraQuantity().equals("null")
            ? parseDouble(invoiceForm.extraQuantity())
            : null;
    var extraUnitPrice =
        invoiceForm.extraUnitPrice() != null && !invoiceForm.extraUnitPrice().isBlank()
            ? new BigDecimal(invoiceForm.extraUnitPrice().replace(" ", ""))
            : null;
    var extraAmount =
        invoiceForm.extraAmount() != null && !invoiceForm.extraAmount().isBlank()
            ? new BigDecimal(invoiceForm.extraAmount().replace(" ", ""))
            : null;
    var total =
        invoiceForm.total() != null && !invoiceForm.total().isBlank()
            ? new BigDecimal(invoiceForm.total().replace(" ", ""))
            : null;

    return new InvoiceForm(
        yearMonth,
        reference,
        issueDate,
        invoiceForm.description(),
        quantity,
        unitPrice,
        amount,
        invoiceForm.hasUpgradedLevel(),
        invoiceForm.extraDescription(),
        extraQuantity,
        extraUnitPrice,
        extraAmount,
        total,
        invoiceForm.parsedAmount(),
        invoiceForm.rib());
  }
}
