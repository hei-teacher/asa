package school.hei.asa.model;

import java.time.YearMonth;

public record InvoiceReference(
    String id, YearMonth yearMonth, Integer autoincrement, Worker worker) {}
