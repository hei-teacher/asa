package school.hei.asa.repository.jrepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JInvoiceDetails;

@Repository
public interface JInvoiceDetailsRepository extends JpaRepository<JInvoiceDetails, String> {
  @Override
  List<JInvoiceDetails> findAll();

  List<JInvoiceDetails> findByWorkerCode(String workerCode);
}
