package school.hei.asa.repository.jrepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.asa.repository.model.JPaySlip;

public interface JPaySlipRepository extends JpaRepository<JPaySlip, String> {
  Optional<JPaySlip> findByWorkerCodeAndYearMonth(String workerCode, String yearMonth);
}
