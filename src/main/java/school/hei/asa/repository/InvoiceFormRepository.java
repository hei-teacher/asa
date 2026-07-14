package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.repository.jrepository.JInvoiceDataRepository;
import school.hei.asa.repository.mapper.InvoiceFormMapper;

@Repository
@AllArgsConstructor
public class InvoiceFormRepository {
  private JInvoiceDataRepository invoiceDataRepository;
  private InvoiceFormMapper invoiceFormMapper;

  @Transactional
  public void saveInvoiceForm(InvoiceForm invoiceForm) {
    invoiceDataRepository.save(invoiceFormMapper.toEntity(invoiceForm));
  }
}
