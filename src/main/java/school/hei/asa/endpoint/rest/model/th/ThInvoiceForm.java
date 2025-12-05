package school.hei.asa.endpoint.rest.model.th;

public record ThInvoiceForm(
    String yearMonth,
    String reference,
    String issueDate,
    String description,
    String quantity,
    String unitPrice,
    String amount,
    Boolean hasUpgradedLevel,
    String extraDescription,
    String extraQuantity,
    String extraUnitPrice,
    String extraAmount,
    String total,
    String parsedAmount,
    String rib) {}
