package school.hei.asa.endpoint.rest.model.th;

import java.time.YearMonth;

public record ThMonthInvoiceStatus(YearMonth yearMonth, boolean hasInvoice) {}
