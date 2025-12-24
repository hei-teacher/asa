package school.hei.asa.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public record InvoiceForm(
    YearMonth yearMonth,
    LocalDate referenceDate,
    LocalDate issueDate,
    String description,
    Double quantity,
    BigDecimal unitPrice,
    BigDecimal amount,
    Boolean hasUpgradedLevel,
    String extraDescription,
    Double extraQuantity,
    BigDecimal extraUnitPrice,
    BigDecimal extraAmount,
    BigDecimal total,
    String parsedAmount,
    String rib) {}
