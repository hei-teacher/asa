package school.hei.asa.repository.jrepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JWorker;

@Repository
public interface JWorkerRepository extends JpaRepository<JWorker, String> {
  @Override
  List<JWorker> findAll();

  JWorker findByCode(String code);

  Optional<JWorker> findByEmail(String email);

  @Query(
      """
SELECT distinct w FROM JWorker w
JOIN JContract c ON c.worker = w
WHERE ((EXTRACT(YEAR FROM c.endInstant) >= ?1) or (c.endInstant is null))
AND (EXTRACT(year from c.entranceInstant) < ?1 +1)
""")
  List<JWorker> findByYear(int year);
}
