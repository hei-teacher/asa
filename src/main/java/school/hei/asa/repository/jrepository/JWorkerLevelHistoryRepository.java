package school.hei.asa.repository.jrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JWorker;
import school.hei.asa.repository.model.JWorkerLevelHistory;

import java.util.List;

@Repository
public interface JWorkerLevelHistoryRepository extends JpaRepository<JWorkerLevelHistory, String> {
  @Override
  List<JWorkerLevelHistory> findAll();

  List<JWorkerLevelHistory> findAllByWorker(JWorker jWorker);
}
