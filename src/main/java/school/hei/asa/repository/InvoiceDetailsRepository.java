package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.InvoiceDetails;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JInvoiceDetailsRepository;
import school.hei.asa.repository.mapper.InvoiceDetailsMapper;

@AllArgsConstructor
@Repository
public class InvoiceDetailsRepository {
  private final InvoiceDetailsMapper invoiceDetailsMapper;
  private final JInvoiceDetailsRepository jInvoiceDetailsRepository;

  @Transactional
  public List<InvoiceDetails> findInvoiceDetailsByWorker(Worker worker) {
    return jInvoiceDetailsRepository.findByWorkerCode(worker.code()).stream()
        .map(invoiceDetailsMapper::toDomain)
        .toList();
  }

  @Transactional
  public void saveInvoiceDetails(InvoiceDetails invoiceDetails) {
    jInvoiceDetailsRepository.save(invoiceDetailsMapper.toEntity(invoiceDetails));
  }
}
