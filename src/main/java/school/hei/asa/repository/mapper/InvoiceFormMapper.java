package school.hei.asa.repository.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.repository.jrepository.JInvoiceReferenceRepository;
import school.hei.asa.repository.model.JInvoice;

@AllArgsConstructor
@Component
public class InvoiceFormMapper {
  private final JInvoiceReferenceRepository jInvoiceReferenceRepository;

  public JInvoice toEntity(InvoiceForm invoiceForm) {
    var jInvoice = new JInvoice();
    var jInvoiceReference = jInvoiceReferenceRepository.getReferenceById(invoiceForm.id());
    jInvoice.setId(invoiceForm.id());
    jInvoice.setYearMonth(invoiceForm.yearMonth().toString());
    jInvoice.setReferenceDate(invoiceForm.referenceDate());
    jInvoice.setIssueDate(invoiceForm.issueDate());
    jInvoice.setDescription(invoiceForm.description());
    jInvoice.setUnitPrice(invoiceForm.unitPrice());
    jInvoice.setAmount(invoiceForm.amount());
    jInvoice.setHasUpgradedLevel(invoiceForm.hasUpgradedLevel());
    jInvoice.setExtraDescription(invoiceForm.extraDescription());
    jInvoice.setExtraQuantity(invoiceForm.extraQuantity());
    jInvoice.setExtraUnitPrice(invoiceForm.extraUnitPrice());
    jInvoice.setExtraAmount(invoiceForm.extraAmount());
    jInvoice.setTotal(invoiceForm.total());
    jInvoice.setParsedAmount(invoiceForm.parsedAmount());
    jInvoice.setRib(invoiceForm.rib());
    jInvoice.setInvoiceReference(jInvoiceReference);
    return jInvoice;
  }
}
