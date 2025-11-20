package school.hei.asa.model;

import java.time.YearMonth;

public record InvoiceDetails(String id, YearMonth yearMonth, String reference, Worker worker) {}
