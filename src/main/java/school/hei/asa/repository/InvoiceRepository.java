package school.hei.asa.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.repository.jrepository.JInvoiceDataRepository;

@Repository
@AllArgsConstructor
public class InvoiceRepository {
  private JInvoiceDataRepository invoiceDataRepository;

  public InvoiceForm saveInvoice(InvoiceForm invoiceForm) {
    return invoiceDataRepository.save(invoiceForm);
  }
}
