package school.hei.asa.repository.jrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JInvoice;

@Repository
public interface JInvoiceDataRepository extends JpaRepository<JInvoice, String> {}
