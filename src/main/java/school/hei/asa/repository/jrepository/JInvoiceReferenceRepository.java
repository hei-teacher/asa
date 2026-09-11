package school.hei.asa.repository.jrepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JInvoiceReference;

@Repository
public interface JInvoiceReferenceRepository extends JpaRepository<JInvoiceReference, String> {
  @Override
  List<JInvoiceReference> findAll();

  List<JInvoiceReference> findByWorkerCode(String workerCode);

  Optional<JInvoiceReference> findByWorkerCodeAndYearMonth(String workerCode, String yearMonth);
}
