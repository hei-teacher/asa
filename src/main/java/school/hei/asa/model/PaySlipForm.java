package school.hei.asa.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record PaySlipForm(
    String id,
    YearMonth yearMonth,
    BigDecimal grossAmount,
    BigDecimal netAmount,
    List<ResolvedTax> resolvedTaxes,
    BigDecimal employeeTotalTaxAmount,
    BigDecimal deductionTotalAmount,
    BigDecimal deductionAndTaxTotal,
    BigDecimal employerTotalTaxAmount,
    BigDecimal taxableGrossAmount,
    PaidLeave paidLeave,
    BigDecimal amountAfterTaxes,
    List<EarnedCredit> credits,
    BigDecimal reductionForDependents)
    implements GeneratedDocument {}
