package school.hei.asa.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record PaySlipForm(
    String id,
    YearMonth yearMonth,
    BigDecimal grossAmount,
    BigDecimal netAmount,
    List<Tax> taxes,
    BigDecimal employeeTotalTaxAmount,
    BigDecimal employerTotalTaxAmount,
    BigDecimal taxableGrossAmount,
    PaidLeave paidLeave,
    BigDecimal amountAfterTaxes,
    List<EarnedCredit> credits)
    implements GeneratedDocument {}
