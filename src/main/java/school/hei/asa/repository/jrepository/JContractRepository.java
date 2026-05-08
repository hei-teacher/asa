package school.hei.asa.repository.jrepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JContract;
import school.hei.asa.repository.model.JWorker;

@Repository
public interface JContractRepository extends JpaRepository<JContract, String> {

  List<JContract> findAllByWorkerOrderByEntranceInstantDesc(JWorker jWorker);

  @Query(
      """
      SELECT c FROM JContract c
      WHERE ((EXTRACT(YEAR FROM c.endInstant) >= ?1) or (c.endInstant is null))
      AND (EXTRACT(year from c.entranceInstant) < ?2)
      ORDER BY c.entranceInstant DESC
      """)
  List<JContract> findByYearBetween(int startYear, int endYear);

  @Query("SELECT c FROM JContract c WHERE c.endInstant IS NULL AND c.durationInDays != 0")
  List<JContract> findActiveContracts();
}
