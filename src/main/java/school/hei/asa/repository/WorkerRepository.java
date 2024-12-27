package school.hei.asa.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.StudentContractor;
import school.hei.asa.model.Worker;

@Repository
public class WorkerRepository {
  public List<Worker> findAll() {
    return List.of(new StudentContractor("lita-code", "Lita"));
  }
}
