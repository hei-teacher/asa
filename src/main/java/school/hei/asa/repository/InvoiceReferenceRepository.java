package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JInvoiceReferenceRepository;
import school.hei.asa.repository.mapper.InvoiceDetailsMapper;

@AllArgsConstructor
@Repository
public class InvoiceReferenceRepository {
  private final InvoiceDetailsMapper invoiceDetailsMapper;
  private final JInvoiceReferenceRepository jInvoiceReferenceRepository;

  @Transactional
  public List<InvoiceReference> findInvoiceReferenceByWorker(Worker worker) {
    return jInvoiceReferenceRepository.findByWorkerCode(worker.code()).stream()
        .map(invoiceDetailsMapper::toDomain)
        .toList();
  }

  @Transactional
  public void saveInvoiceReference(InvoiceReference invoiceReference) {
    jInvoiceReferenceRepository.save(invoiceDetailsMapper.toEntity(invoiceReference));
  }
}
