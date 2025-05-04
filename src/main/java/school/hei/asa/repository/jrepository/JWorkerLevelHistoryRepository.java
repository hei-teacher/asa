package school.hei.asa.repository.jrepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JWorker;
import school.hei.asa.repository.model.JWorkerLevelHistory;

@Repository
public interface JWorkerLevelHistoryRepository extends JpaRepository<JWorkerLevelHistory, String> {

  List<JWorkerLevelHistory> findAllByWorkerOrderByEntranceInstantDesc(JWorker jWorker);
}
