package school.hei.asa.endpoint.rest.model.th;

public record ThInvoiceForm(
    String yearMonth,
    String reference,
    String issueDate,
    String description,
    String quantity,
    String unitPrice,
    String amount,
    String total,
    String parsedAmount,
    String rib) {}
