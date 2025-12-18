package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JInvoiceDetailsRepository;
import school.hei.asa.repository.mapper.InvoiceDetailsMapper;

@AllArgsConstructor
@Repository
public class InvoiceDetailsRepository {
  private final InvoiceDetailsMapper invoiceDetailsMapper;
  private final JInvoiceDetailsRepository jInvoiceDetailsRepository;

  @Transactional
  public List<InvoiceReference> findInvoiceDetailsByWorker(Worker worker) {
    return jInvoiceDetailsRepository.findByWorkerCode(worker.code()).stream()
        .map(invoiceDetailsMapper::toDomain)
        .toList();
  }

  @Transactional
  public void saveInvoiceDetails(InvoiceReference invoiceReference) {
    jInvoiceDetailsRepository.save(invoiceDetailsMapper.toEntity(invoiceReference));
  }
}
