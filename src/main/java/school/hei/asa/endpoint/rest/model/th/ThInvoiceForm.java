package school.hei.asa.endpoint.rest.model.th;

public record ThInvoiceForm(
    String reference,
    String issueDate,
    String description,
    String quantity,
    String unitPrice,
    String amount,
    String total,
    Boolean hasBonus,
    String bonusDescription,
    String bonusQuantity,
    String bonusUnitPrice,
    String bonusAmount,
    String parsedAmount) {}
