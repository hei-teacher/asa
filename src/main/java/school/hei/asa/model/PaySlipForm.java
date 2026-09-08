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
    BigDecimal totalAmount,
    int paidLeaveAmount,
    int takenPaidLeave,
    BigDecimal leaveBaseAmount,
    BigDecimal leaveAmount,
    List<Credit> credits)
    implements GeneratedDocument {}
