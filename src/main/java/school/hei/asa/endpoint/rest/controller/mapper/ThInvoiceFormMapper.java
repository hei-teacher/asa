package school.hei.asa.endpoint.rest.controller.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.service.utils.NumberConverter;
import school.hei.asa.service.utils.NumberParser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static java.lang.Double.parseDouble;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.FRENCH;

@AllArgsConstructor
@Component
public class ThInvoiceFormMapper {
    private final NumberParser numberParser;
    private final DateTimeFormatter yearMonthFormatter = ofPattern("yyyy-MM");
    private final DateTimeFormatter localDateFormatter = ofPattern("dd/MM/yyyy", FRENCH);

    public ThInvoiceForm toTh(InvoiceForm invoiceForm){
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
                invoiceForm.rib()
        );
    }

    public InvoiceForm toDomain(ThInvoiceForm invoiceForm){
        return new InvoiceForm(
            YearMonth.parse(invoiceForm.yearMonth(), yearMonthFormatter),
                LocalDate.parse(invoiceForm.reference(), localDateFormatter),
                LocalDate.parse(invoiceForm.issueDate(), localDateFormatter),
                invoiceForm.description(),
                parseDouble(invoiceForm.quantity()),
                new BigDecimal(invoiceForm.unitPrice()),
                new BigDecimal(invoiceForm.amount()),
                invoiceForm.hasUpgradedLevel(),
                invoiceForm.extraDescription(),
                parseDouble(invoiceForm.extraQuantity()),
                new BigDecimal(invoiceForm.extraUnitPrice()),
                new BigDecimal(invoiceForm.extraAmount()),
                new BigDecimal(invoiceForm.total()),
                invoiceForm.parsedAmount(),
                invoiceForm.rib()
                );
    }
}
