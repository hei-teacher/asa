package school.hei.asa.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.repository.model.JInvoice;

@Component
public class InvoiceFormMapper {
  public InvoiceForm toEntity(InvoiceForm invoiceForm) {
    return new JInvoice(
        invoiceForm.id(),
        invoiceForm.yearMonth(),
        invoiceForm.referenceDate(),
        invoiceForm.issueDate(),
        invoiceForm.description(),
        invoiceForm.unitPrice(),
        invoiceForm.amount(),
        invoiceForm.hasUpgradedLevel(),
        invoiceForm.extraQuantity(),
        invoiceForm.extraUnitPrice(),
        invoiceForm.extraAmount(),
        invoiceForm.total(),
        invoiceForm.parsedAmount(),
        invoiceForm.rib());
  }
}
