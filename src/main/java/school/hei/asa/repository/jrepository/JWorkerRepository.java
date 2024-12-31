package school.hei.asa.repository.jrepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JWorker;

@Repository
public interface JWorkerRepository extends JpaRepository<JWorker, String> {
  @Override
  List<JWorker> findAll();

  JWorker findByCode(String code);

  Optional<JWorker> findByEmail(String email);
}
