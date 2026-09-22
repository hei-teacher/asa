package school.hei.asa.repository.jrepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.asa.repository.model.JEarnedCredits;
import school.hei.asa.repository.model.JWorker;

public interface JEarnedCreditsRepository extends JpaRepository<JEarnedCredits, String> {

  List<JEarnedCredits> findAllByWorkerAndYearMonth(JWorker worker, String yearMonth);
}
