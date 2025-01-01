package school.hei.asa.repository.jrepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JMissionExecution;
import school.hei.asa.repository.model.JWorker;

@Repository
public interface JMissionExecutionRepository extends JpaRepository<JMissionExecution, String> {
  @Override
  List<JMissionExecution> findAll();

  List<JMissionExecution> findAllByWorker(JWorker jWorker);
}
